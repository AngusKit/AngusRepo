package cloud.xcan.angus.core.gm.application.query.group.impl;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.group.GroupRepo;
import cloud.xcan.angus.api.commonlink.group.GroupUser;
import cloud.xcan.angus.api.commonlink.group.GroupUserRepo;
import cloud.xcan.angus.api.commonlink.group.enums.GroupType;
import cloud.xcan.angus.api.commonlink.user.UserBase;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.domain.group.GroupSearchRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class GroupQueryImpl implements GroupQuery {

  @Resource
  private GroupRepo groupRepo;

  @Resource
  private GroupSearchRepo groupSearchRepo;

  @Resource
  private GroupUserRepo groupUserRepo;

  @Resource
  private UserManager userManager;

  @Override
  public Group findAndCheck(Long id) {
    return new BizTemplate<Group>() {
      @Override
      protected Group process() {
        return groupRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("组未找到", new Object[]{}));
      }
    }.execute();
  }

  @Override
  public Page<Group> find(GenericSpecification<Group> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Group>>() {
      @Override
      protected Page<Group> process() {
        return fullTextSearch
            ? groupSearchRepo.find(spec.getCriteria(), pageable, Group.class, match)
            : groupRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public List<Group> findByUserId(Long userId) {
    return new BizTemplate<List<Group>>() {
      @Override
      protected List<Group> process() {
        // Find group-user relations by userId
        List<GroupUser> groupUsers = groupUserRepo.findAllByUserId(userId);

        if (groupUsers.isEmpty()) {
          return new ArrayList<>();
        }

        // Extract group IDs
        Set<Long> groupIds = groupUsers.stream()
            .map(GroupUser::getGroupId)
            .collect(Collectors.toSet());

        // Find groups by IDs
        return groupRepo.findAllById(groupIds);
      }
    }.execute();
  }

  @Override
  public long countNewGroupsThisMonth() {
    return new BizTemplate<Long>() {
      @Override
      protected Long process() {
        LocalDateTime startOfMonth = LocalDateTime.now()
            .with(TemporalAdjusters.firstDayOfMonth())
            .withHour(0).withMinute(0).withSecond(0).withNano(0);

        // Count groups created this month
        return groupRepo.findAll().stream()
            .filter(group -> group.getCreatedDate() != null
                && group.getCreatedDate().isAfter(startOfMonth))
            .count();
      }
    }.execute();
  }

  @Override
  public boolean existsByCode(String code) {
    return groupRepo.existsByCode(code);
  }

  @Override
  public long count() {
    return groupRepo.count();
  }

  @Override
  public long countByType(GroupType type) {
    return groupRepo.countByType(type);
  }

  @Override
  public void setOwnerUser(List<Group> groups) {
    if (isEmpty(groups)) {
      return;
    }

    Set<Long> ownerIds = groups.stream().map(Group::getOwnerId).collect(Collectors.toSet());
    if (isEmpty(ownerIds)) {
      return;
    }

    Map<Long, UserBase> userBaseMap = userManager.getUserBaseMap(ownerIds);
    for (Group group : groups) {
      if (group.getOwnerId() != null) {
        UserBase owner = userBaseMap.get(group.getOwnerId());
        if (owner != null) {
          group.setOwner(owner);
        }
      }
    }
  }

  @Override
  public List<Group> findAllById(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return new ArrayList<>();
    }
    return groupRepo.findAllById(ids);
  }
}
