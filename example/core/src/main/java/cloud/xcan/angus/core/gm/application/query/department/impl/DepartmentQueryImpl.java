package cloud.xcan.angus.core.gm.application.query.department.impl;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.department.Department;
import cloud.xcan.angus.api.commonlink.department.DepartmentRepo;
import cloud.xcan.angus.api.commonlink.department.DepartmentUser;
import cloud.xcan.angus.api.commonlink.department.DepartmentUserRepo;
import cloud.xcan.angus.api.commonlink.user.UserBase;
import cloud.xcan.angus.api.commonlink.user.UserBaseRepo;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentQuery;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentUserQuery;
import cloud.xcan.angus.core.gm.domain.department.DepartmentSearchRepo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class DepartmentQueryImpl implements DepartmentQuery {

  @Resource
  private DepartmentRepo departmentRepo;

  @Resource
  private DepartmentSearchRepo departmentSearchRepo;

  @Resource
  private DepartmentUserRepo departmentUserRepo;

  @Resource
  private DepartmentUserQuery departmentUserQuery;

  @Resource
  private UserBaseRepo userBaseRepo;

  @Resource
  private UserRepo userRepo;

  @Override
  public Department findAndCheck(Long id) {
    return new BizTemplate<Department>() {
      @Override
      protected Department process() {
        return departmentRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("部门「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public Page<Department> find(GenericSpecification<Department> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Department>>() {
      @Override
      protected Page<Department> process() {
        Page<Department> page = fullTextSearch
            ? departmentSearchRepo.find(spec.getCriteria(), pageable, Department.class, match)
            : departmentRepo.findAll(spec, pageable);

        if (page.hasContent()) {
          List<Department> departments = page.getContent();
          populateAssociatedData(departments);
        }
        return page;
      }
    }.execute();
  }

  @Override
  public List<Department> findTree(Long parentId, EnabledStatus status, String keyword) {
    return new BizTemplate<List<Department>>() {
      @Override
      protected List<Department> process() {
        List<Department> departments = new ArrayList<>();

        if (parentId == null) {
          // 如果parentId为null，一次性查询所有部门（包括顶级和所有子级）
          if (status != null) {
            // 如果指定了状态，查询所有符合状态的部门
            departments = departmentRepo.findAll().stream()
                .filter(dept -> dept.getStatus() == status)
                .collect(Collectors.toList());
          } else {
            // 如果没有指定状态，查询所有部门
            departments = departmentRepo.findAll();
          }
        } else {
          // 如果parentId不为null，递归查询该部门及其所有子部门
          // 1. 先查询指定部门本身
          Department parentDept = departmentRepo.findById(parentId).orElse(null);
          if (parentDept != null && (status == null || parentDept.getStatus() == status)) {
            departments.add(parentDept);
          }

          // 2. 递归查询所有子部门
          List<Department> allChildren = findChildrenRecursive(parentId, status);
          departments.addAll(allChildren);
        }

        // 如果提供了keyword，过滤匹配的部门（匹配name或code）
        // 优化：如果子级匹配成功，所有父级也需要返回以保持树结构完整性
        if (keyword != null && !keyword.trim().isEmpty()) {
          String keywordLower = keyword.toLowerCase().trim();

          // 先找出所有匹配的部门
          Set<Long> matchedIds = departments.stream()
              .filter(dept -> {
                String name = dept.getName() != null ? dept.getName().toLowerCase() : "";
                String code = dept.getCode() != null ? dept.getCode().toLowerCase() : "";
                return name.contains(keywordLower) || code.contains(keywordLower);
              })
              .map(Department::getId)
              .collect(Collectors.toSet());

          // 收集所有匹配部门的父级ID
          Set<Long> parentIdsToInclude = new HashSet<>();
          for (Department dept : departments) {
            if (matchedIds.contains(dept.getId())) {
              // 如果当前部门匹配，收集其所有父级
              collectParentIds(dept, departments, parentIdsToInclude);
            }
          }

          // 合并匹配的部门ID和需要包含的父级ID
          Set<Long> allIdsToInclude = new HashSet<>(matchedIds);
          allIdsToInclude.addAll(parentIdsToInclude);

          // 过滤出需要返回的部门
          departments = departments.stream()
              .filter(dept -> allIdsToInclude.contains(dept.getId()))
              .collect(Collectors.toList());
        }

        // 设置关联数据
        if (!departments.isEmpty()) {
          populateAssociatedData(departments);
        }

        return departments;
      }
    }.execute();
  }

  @Override
  public DepartmentStatsVo getStats() {
    return new BizTemplate<DepartmentStatsVo>() {
      @Override
      protected DepartmentStatsVo process() {
        DepartmentStatsVo stats = new DepartmentStatsVo();
        LocalDateTime now = LocalDateTime.now();

        // 计算时间范围
        LocalDateTime firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth())
            .withHour(0).withMinute(0).withSecond(0).withNano(0);

        // 总部门数
        long totalDepartments = departmentRepo.count();
        stats.setTotalDepartments(totalDepartments);

        // 计算总部门数变化量（对比上个月）
        long totalDepartmentsLastMonth = departmentRepo.countByCreatedDateBefore(firstDayOfMonth);
        stats.setTotalDepartmentsChange(totalDepartments - totalDepartmentsLastMonth);

        // 已启用部门数
        long enabledDepartments = departmentRepo.countByStatus(EnabledStatus.ENABLED);
        stats.setEnabledDepartments(enabledDepartments);

        // 已禁用部门数
        long disabledDepartments = departmentRepo.countByStatus(EnabledStatus.DISABLED);
        stats.setDisabledDepartments(disabledDepartments);

        // 总用户数
        long totalUsers = userBaseRepo.count();
        stats.setTotalUsers(totalUsers);

        // 最大层级深度
        Integer maxLevel = departmentRepo.findMaxLevel();
        stats.setMaxLevel(maxLevel != null ? maxLevel : 0);

        // 本月新增部门数
        long newDepartmentsThisMonth = departmentRepo.countByCreatedDateAfter(firstDayOfMonth);
        stats.setNewDepartmentsThisMonth(newDepartmentsThisMonth);

        // 统计一级部门数量
        long firstLevelDepartments = departmentRepo.countByParentIdIsNull();
        stats.setFirstLevelDepartments(firstLevelDepartments);

        // 计算一级部门数量变化量（对比上个月）
        // 需要查询上个月末的一级部门数，这里简化处理：查询本月新增的一级部门
        long newFirstLevelDepartmentsThisMonth = departmentRepo.findByParentIdIsNull().stream()
            .filter(dept -> dept.getCreatedDate() != null
                && dept.getCreatedDate().isAfter(firstDayOfMonth))
            .count();
        long firstLevelDepartmentsLastMonth =
            firstLevelDepartments - newFirstLevelDepartmentsThisMonth;
        stats.setFirstLevelDepartmentsChange(
            firstLevelDepartments - firstLevelDepartmentsLastMonth);

        // 计算平均人数
        List<Department> allDepartments = departmentRepo.findAll();
        Map<Long, Long> departmentUserCountMap = new HashMap<>();
        if (!allDepartments.isEmpty()) {
          List<Long> departmentIds = allDepartments.stream()
              .map(Department::getId)
              .collect(Collectors.toList());
          departmentUserCountMap = departmentUserQuery.countUsersByDepartmentIds(departmentIds);
        }

        long totalUsersInDepartments = departmentUserCountMap.values().stream()
            .mapToLong(Long::longValue)
            .sum();
        double averageUsersPerDepartment = totalDepartments > 0
            ? (double) totalUsersInDepartments / totalDepartments
            : 0.0;
        stats.setAverageUsersPerDepartment(Math.round(averageUsersPerDepartment * 10.0) / 10.0);

        // 计算平均人数增长率（对比上个月）
        // 简化处理：使用当前平均人数对比上个月的平均人数
        long totalUsersLastMonth = userRepo.countByCreatedDateBefore(firstDayOfMonth);
        double averageUsersLastMonth = totalDepartmentsLastMonth > 0
            ? (double) totalUsersLastMonth / totalDepartmentsLastMonth
            : 0.0;
        if (averageUsersLastMonth > 0) {
          double averageUsersGrowthRate = ((averageUsersPerDepartment - averageUsersLastMonth)
              / averageUsersLastMonth) * 100;
          stats.setAverageUsersGrowthRate(Math.round(averageUsersGrowthRate * 10.0) / 10.0);
        } else {
          stats.setAverageUsersGrowthRate(0.0);
        }

        // 统计部门规模分布
        List<DepartmentStatsVo.DepartmentSizeDistributionVo> sizeDistribution = new ArrayList<>();
        long countLessThan10 = 0L;
        long count10To30 = 0L;
        long count30To50 = 0L;
        long countMoreThan50 = 0L;

        for (Map.Entry<Long, Long> entry : departmentUserCountMap.entrySet()) {
          long userCount = entry.getValue();
          if (userCount < 10) {
            countLessThan10++;
          } else if (userCount < 30) {
            count10To30++;
          } else if (userCount < 50) {
            count30To50++;
          } else {
            countMoreThan50++;
          }
        }

        DepartmentStatsVo.DepartmentSizeDistributionVo dist1 = new DepartmentStatsVo.DepartmentSizeDistributionVo();
        dist1.setSizeRange("10人以下");
        dist1.setCount(countLessThan10);
        sizeDistribution.add(dist1);

        DepartmentStatsVo.DepartmentSizeDistributionVo dist2 = new DepartmentStatsVo.DepartmentSizeDistributionVo();
        dist2.setSizeRange("10-30人");
        dist2.setCount(count10To30);
        sizeDistribution.add(dist2);

        DepartmentStatsVo.DepartmentSizeDistributionVo dist3 = new DepartmentStatsVo.DepartmentSizeDistributionVo();
        dist3.setSizeRange("30-50人");
        dist3.setCount(count30To50);
        sizeDistribution.add(dist3);

        DepartmentStatsVo.DepartmentSizeDistributionVo dist4 = new DepartmentStatsVo.DepartmentSizeDistributionVo();
        dist4.setSizeRange("50人以上");
        dist4.setCount(countMoreThan50);
        sizeDistribution.add(dist4);

        stats.setSizeDistribution(sizeDistribution);

        // 统计部门层级分布
        List<DepartmentStatsVo.DepartmentLevelDistributionVo> levelDistribution = new ArrayList<>();
        if (maxLevel != null && maxLevel > 0) {
          for (int level = 1; level <= maxLevel; level++) {
            long count = departmentRepo.countByLevel(level);
            if (count > 0) {
              DepartmentStatsVo.DepartmentLevelDistributionVo levelDist
                  = new DepartmentStatsVo.DepartmentLevelDistributionVo();
              levelDist.setLevelName(level + "级部门");
              levelDist.setCount(count);
              levelDistribution.add(levelDist);
            }
          }
        }
        stats.setLevelDistribution(levelDistribution);
        return stats;
      }
    }.execute();
  }

  @Override
  public List<Department> findChildren(Long parentId, Boolean recursive) {
    return new BizTemplate<List<Department>>() {
      @Override
      protected List<Department> process() {
        if (parentId == null) {
          return List.of();
        }

        List<Department> children = departmentRepo.findByParentId(parentId);

        if (Boolean.TRUE.equals(recursive) && !children.isEmpty()) {
          List<Department> allChildren = new java.util.ArrayList<>(children);
          for (Department child : children) {
            allChildren.addAll(findChildren(child.getId(), true));
          }
          // 设置关联数据
          if (!allChildren.isEmpty()) {
            populateAssociatedData(allChildren);
          }
          return allChildren;
        }

        // 设置关联数据
        if (!children.isEmpty()) {
          populateAssociatedData(children);
        }
        return children;
      }
    }.execute();
  }

  @Override
  public List<Department> getPath(Long id) {
    return new BizTemplate<List<Department>>() {
      @Override
      protected List<Department> process() {
        List<Department> path = new ArrayList<>();
        // Build path from current department to root
        Department current = findAndCheck(id);
        while (current != null) {
          path.add(0, current); // Add to beginning
          if (current.getParentId() != null) {
            current = departmentRepo.findById(current.getParentId()).orElse(null);
          } else {
            current = null;
          }
        }
        return path;
      }
    }.execute();
  }

  @Override
  public List<Department> findByUserId(Long userId) {
    return new BizTemplate<List<Department>>() {
      @Override
      protected List<Department> process() {
        List<Long> departmentIds = departmentUserRepo.findByUserId(userId).stream()
            .map(DepartmentUser::getDepartmentId)
            .distinct()
            .collect(Collectors.toList());
        return departmentIds.isEmpty()
            ? new ArrayList<>() : departmentRepo.findAllById(departmentIds);
      }
    }.execute();
  }

  /**
   * 递归查询指定部门的所有子部门
   *
   * @param parentId 父部门ID
   * @param status   状态筛选（可选）
   * @return 所有子部门列表
   */
  private List<Department> findChildrenRecursive(Long parentId, EnabledStatus status) {
    List<Department> allChildren = new ArrayList<>();

    // 查询直接子部门
    List<Department> directChildren;
    if (status != null) {
      directChildren = departmentRepo.findByParentIdAndStatus(parentId, status);
    } else {
      directChildren = departmentRepo.findByParentId(parentId);
    }

    if (!directChildren.isEmpty()) {
      allChildren.addAll(directChildren);

      // 递归查询每个子部门的子部门
      for (Department child : directChildren) {
        allChildren.addAll(findChildrenRecursive(child.getId(), status));
      }
    }
    return allChildren;
  }

  @Override
  public boolean existsByCode(String code) {
    return departmentRepo.existsByCode(code);
  }

  @Override
  public Long countByTenantId(Long tenantId) {
    return departmentRepo.countByTenantId(tenantId);
  }

  /**
   * 批量设置关联数据（负责人名称、用户数量、父部门名称等）
   */
  private void populateAssociatedData(List<Department> departments) {
    if (departments == null || departments.isEmpty()) {
      return;
    }

    // 收集需要查询的ID
    Set<Long> leaderIds = departments.stream()
        .map(Department::getLeaderId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    Set<Long> parentIds = departments.stream()
        .map(Department::getParentId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    Set<Long> departmentIds = departments.stream()
        .map(Department::getId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    // 批量查询负责人信息
    Map<Long, UserBase> leaderMap = Collections.emptyMap();
    if (!leaderIds.isEmpty()) {
      List<UserBase> leaders = userBaseRepo.findAllById(leaderIds);
      leaderMap = leaders.stream().collect(
          Collectors.toMap(UserBase::getId, user -> user, (existing, replacement) -> existing));
    }

    // 批量查询父部门信息
    Map<Long, Department> parentMap = Collections.emptyMap();
    if (!parentIds.isEmpty()) {
      List<Department> parents = departmentRepo.findAllById(parentIds);
      parentMap = parents.stream()
          .collect(Collectors.toMap(Department::getId, dept -> dept,
              (existing, replacement) -> existing));
    }

    // 批量查询用户数量
    Map<Long, Long> userCountMap = Collections.emptyMap();
    if (!departmentIds.isEmpty()) {
      userCountMap = departmentUserQuery.countUsersByDepartmentIds(departmentIds);
    }

    // 设置关联数据
    for (Department dept : departments) {
      // 设置负责人信息
      if (dept.getLeaderId() != null) {
        UserBase leader = leaderMap.get(dept.getLeaderId());
        if (leader != null) {
          dept.setLeaderName(leader.getName());
          dept.setLeaderAvatar(leader.getAvatar());
        }
      }

      // 设置父部门名称
      if (dept.getParentId() != null) {
        Department parent = parentMap.get(dept.getParentId());
        if (parent != null) {
          dept.setParentName(parent.getName());
        }
      }

      // 设置用户数量
      Long userCount = userCountMap.get(dept.getId());
      dept.setUserCount(userCount != null ? userCount : 0L);
    }
  }

  @Override
  public List<Department> findAllById(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return new ArrayList<>();
    }
    return departmentRepo.findAllById(ids);
  }

  /**
   * 收集部门的所有父级ID
   *
   * @param dept               当前部门
   * @param allDepartments     所有部门列表
   * @param parentIdsToInclude 需要包含的父级ID集合
   */
  private void collectParentIds(Department dept, List<Department> allDepartments,
      Set<Long> parentIdsToInclude) {
    Long currentParentId = dept.getParentId();
    while (currentParentId != null) {
      parentIdsToInclude.add(currentParentId);
      // 查找父部门
      final Long parentIdToFind = currentParentId;
      Department parentDept = allDepartments.stream()
          .filter(d -> parentIdToFind.equals(d.getId()))
          .findFirst()
          .orElse(null);
      if (parentDept != null) {
        currentParentId = parentDept.getParentId();
      } else {
        break;
      }
    }
  }

}
