package cloud.xcan.angus.core.gm.application.query.group.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.group.GroupUser;
import cloud.xcan.angus.api.commonlink.group.GroupUserRepo;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.application.query.group.GroupUserQuery;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.remote.search.SearchOperation;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class GroupUserQueryImpl implements GroupUserQuery {

  @Resource
  private GroupQuery groupQuery;

  @Resource
  private GroupUserRepo groupUserRepo;

  @Resource
  private UserRepo userRepo;

  @Override
  public Page<User> findUsers(Long groupId, GenericSpecification<User> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<User>>() {
      Group existing;

      @Override
      protected void checkParams() {
        existing = groupQuery.findAndCheck(groupId);
      }

      @Override
      protected Page<User> process() {
        List<GroupUser> groupUsers = groupUserRepo.findAllByGroupId(groupId);
        if (groupUsers.isEmpty()) {
          return Page.empty(pageable);
        }

        List<Long> userIds = groupUsers.stream()
            .map(GroupUser::getUserId)
            .collect(Collectors.toList());
        Set<SearchCriteria> criteria = new HashSet<>(spec.getCriteria());
        criteria.add(new SearchCriteria("id", userIds, SearchOperation.IN));
        GenericSpecification<User> finalSpec = new GenericSpecification<>(criteria);
        Page<User> userPage = userRepo.findAll(finalSpec, pageable);
        if (userPage.isEmpty()) {
          return userPage;
        }

        for (User user : userPage.getContent()) {
          user.setGroupOwner(user.getId().equals(existing.getOwnerId()));
          user.setGroupJoinDate(
              groupUsers.stream()
                  .filter(gu -> gu.getUserId().equals(user.getId()))
                  .findFirst()
                  .map(GroupUser::getCreatedDate)
                  .orElse(null)
          );
        }
        return userPage;
      }
    }.execute();
  }

  @Override
  public Page<User> findUsersNotInGroup(Long groupId, GenericSpecification<User> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<User>>() {
      @Override
      protected void checkParams() {
        // 检查组是否存在
        groupQuery.findAndCheck(groupId);
      }

      @Override
      protected Page<User> process() {
        // 获取该组的所有用户ID
        List<GroupUser> groupUsers = groupUserRepo.findAllByGroupId(groupId);
        List<Long> userIdsInGroup = groupUsers.stream()
            .map(GroupUser::getUserId)
            .collect(Collectors.toList());

        // 构建查询条件：排除已在组中的用户
        Set<SearchCriteria> criteria = new HashSet<>(spec.getCriteria());
        if (!userIdsInGroup.isEmpty()) {
          criteria.add(new SearchCriteria("id", userIdsInGroup, SearchOperation.NOT_IN));
        }

        GenericSpecification<User> finalSpec = new GenericSpecification<>(criteria);
        return userRepo.findAll(finalSpec, pageable);
      }
    }.execute();
  }

  @Override
  public long countActiveUsers() {
    return new BizTemplate<Long>() {
      @Override
      protected Long process() {
        // 查询当前租户下的所有组用户关系
        Long tenantId = getOptTenantId();
        List<GroupUser> groupUsers = groupUserRepo.findByTenantId(tenantId);

        if (groupUsers.isEmpty()) {
          return 0L;
        }

        // 提取所有用户ID并去重
        Set<Long> userIds = groupUsers.stream()
            .map(GroupUser::getUserId)
            .collect(Collectors.toSet());

        if (userIds.isEmpty()) {
          return 0L;
        }

        // 根据用户ID列表查询在线用户（状态为ACTIVE且online=true）
        List<String> onlineUsernames = userRepo.findUsernamesByIdAndOnline(
            userIds, true);
        // 返回在线用户数量（已去重）
        return (long) onlineUsernames.size();
      }
    }.execute();
  }

  @Override
  public Map<Long, Long> countUsersByGroupIds(List<Long> groupIds) {
    return new BizTemplate<Map<Long, Long>>() {
      @Override
      protected Map<Long, Long> process() {
        if (groupIds == null || groupIds.isEmpty()) {
          return new HashMap<>();
        }

        // 使用SQL分组统计每个组的用户数
        List<Object[]> results = groupUserRepo.countGroupByGroupIds(groupIds);

        // 转换为Map，key为groupId，value为用户数
        Map<Long, Long> countMap = new HashMap<>();
        for (Object[] result : results) {
          Long groupId = ((Number) result[0]).longValue();
          Long userCount = ((Number) result[1]).longValue();
          countMap.put(groupId, userCount);
        }

        // 对于没有用户的组，设置用户数为0
        for (Long groupId : groupIds) {
          countMap.putIfAbsent(groupId, 0L);
        }

        return countMap;
      }
    }.execute();
  }

  @Override
  public List<Long> findUserIdsByGroupId(Long groupId) {
    List<GroupUser> groupUsers = groupUserRepo.findAllByGroupId(groupId);
    return groupUsers.stream()
        .map(GroupUser::getUserId)
        .collect(Collectors.toList());
  }

  @Override
  public boolean existsByGroupIdAndUserId(Long groupId, Long userId) {
    return groupUserRepo.existsByGroupIdAndUserId(groupId, userId);
  }

  @Override
  public List<GroupUser> findByUserIdIn(List<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return new ArrayList<>();
    }
    // GroupUserRepo没有findAllByUserIdIn方法，通过租户ID查询然后过滤
    Long tenantId = getOptTenantId();
    List<GroupUser> allGroupUsers = groupUserRepo.findByTenantId(tenantId);
    Set<Long> userIdSet = new HashSet<>(userIds);
    return allGroupUsers.stream()
        .filter(gu -> userIdSet.contains(gu.getUserId()))
        .collect(Collectors.toList());
  }

}
