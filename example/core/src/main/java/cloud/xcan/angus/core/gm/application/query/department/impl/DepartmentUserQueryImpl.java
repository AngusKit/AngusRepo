package cloud.xcan.angus.core.gm.application.query.department.impl;

import cloud.xcan.angus.api.commonlink.department.DepartmentUser;
import cloud.xcan.angus.api.commonlink.department.DepartmentUserRepo;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentQuery;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentUserQuery;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.remote.search.SearchOperation;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class DepartmentUserQueryImpl implements DepartmentUserQuery {

  @Resource
  private DepartmentUserRepo departmentUserRepo;

  @Resource
  private DepartmentQuery departmentQuery;

  @Resource
  private UserRepo userRepo;

  @Override
  public DepartmentUser findAndCheck(Long id) {
    return new BizTemplate<DepartmentUser>() {
      @Override
      protected DepartmentUser process() {
        return departmentUserRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("部门用户关系「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public Page<User> findUsers(Long departmentId, GenericSpecification<User> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<User>>() {
      @Override
      protected void checkParams() {
        departmentQuery.findAndCheck(departmentId);
      }

      @Override
      protected Page<User> process() {
        // 获取部门的所有用户ID
        List<DepartmentUser> users = departmentUserRepo.findByDepartmentId(departmentId);
        Set<Long> userIds = users.stream()
            .map(DepartmentUser::getUserId)
            .collect(Collectors.toSet());

        if (userIds.isEmpty()) {
          return Page.empty(pageable);
        }

        // 构建包含用户ID的查询条件
        Set<SearchCriteria> criteriaSet = new HashSet<>(spec.getCriteria());
        criteriaSet.add(new SearchCriteria("id", userIds, SearchOperation.IN));
        GenericSpecification<User> finalSpec = new GenericSpecification<>(criteriaSet);
        return userRepo.findAll(finalSpec, pageable);
      }
    }.execute();
  }

  @Override
  public Page<User> findUsersNotInDepartment(Long departmentId, GenericSpecification<User> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<User>>() {
      @Override
      protected void checkParams() {
        // 检查部门是否存在
        departmentQuery.findAndCheck(departmentId);
      }

      @Override
      protected Page<User> process() {
        // 获取该部门的所有用户ID
        List<DepartmentUser> departmentUsers = departmentUserRepo.findByDepartmentId(departmentId);
        List<Long> userIdsInDepartment = departmentUsers.stream()
            .map(DepartmentUser::getUserId)
            .collect(Collectors.toList());

        // 构建查询条件：排除已在部门中的用户
        Set<SearchCriteria> criteria = new HashSet<>(spec.getCriteria());
        if (!userIdsInDepartment.isEmpty()) {
          criteria.add(new SearchCriteria("id", userIdsInDepartment, SearchOperation.NOT_IN));
        }

        GenericSpecification<User> finalSpec = new GenericSpecification<>(criteria);
        return userRepo.findAll(finalSpec, pageable);
      }
    }.execute();
  }

  @Override
  public Map<Long, Long> countUsersByDepartmentIds(Collection<Long> departmentIds) {
    return new BizTemplate<Map<Long, Long>>() {
      @Override
      protected Map<Long, Long> process() {
        if (departmentIds == null || departmentIds.isEmpty()) {
          return new HashMap<>();
        }

        // 使用SQL分组统计每个部门的用户数
        List<Object[]> results = departmentUserRepo.countGroupByDepartmentIds(departmentIds);

        // 转换为Map，key为departmentId，value为用户数
        Map<Long, Long> countMap = new HashMap<>();
        for (Object[] result : results) {
          Long departmentId = ((Number) result[0]).longValue();
          Long userCount = ((Number) result[1]).longValue();
          countMap.put(departmentId, userCount);
        }

        // 对于没有用户的部门，设置用户数为0
        for (Long departmentId : departmentIds) {
          countMap.putIfAbsent(departmentId, 0L);
        }

        return countMap;
      }
    }.execute();
  }

  @Override
  public Optional<DepartmentUser> findByDepartmentIdAndUserId(Long departmentId, Long userId) {
    return departmentUserRepo.findByDepartmentIdAndUserId(departmentId, userId);
  }

  @Override
  public List<DepartmentUser> findByDepartmentId(Long departmentId) {
    return departmentUserRepo.findByDepartmentId(departmentId);
  }

  @Override
  public List<DepartmentUser> findByUserId(Long userId) {
    return departmentUserRepo.findByUserId(userId);
  }

  @Override
  public boolean existsByDepartmentIdAndUserId(Long departmentId, Long userId) {
    return departmentUserRepo.existsByDepartmentIdAndUserId(departmentId, userId);
  }

  @Override
  public Optional<DepartmentUser> findPrimaryByUserId(Long userId) {
    return departmentUserRepo.findByUserIdAndIsPrimaryTrue(userId);
  }

  @Override
  public List<DepartmentUser> findByUserIdIn(List<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return new ArrayList<>();
    }
    return departmentUserRepo.findAllByUserIdIn(userIds);
  }

}
