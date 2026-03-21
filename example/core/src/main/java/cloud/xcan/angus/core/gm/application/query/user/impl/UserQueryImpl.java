package cloud.xcan.angus.core.gm.application.query.user.impl;

import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.isEmail;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.lang.Long.parseLong;

import cloud.xcan.angus.api.commonlink.department.Department;
import cloud.xcan.angus.api.commonlink.department.DepartmentRepo;
import cloud.xcan.angus.api.commonlink.department.DepartmentUser;
import cloud.xcan.angus.api.commonlink.role.Role;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.commonlink.user.enums.InviteStatus;
import cloud.xcan.angus.api.commonlink.user.enums.UserStatus;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.application.query.authorization.AuthorizationQuery;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentUserQuery;
import cloud.xcan.angus.core.gm.application.query.group.GroupUserQuery;
import cloud.xcan.angus.core.gm.application.query.role.RoleQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.user.LoginHistoryRepo;
import cloud.xcan.angus.core.gm.domain.user.UserInviteRepo;
import cloud.xcan.angus.core.gm.domain.user.UserSearchRepo;
import cloud.xcan.angus.api.gm.user.vo.UserStatsVo;
import cloud.xcan.angus.core.jpa.criteria.CriteriaUtils;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.core.utils.PrincipalContextUtils;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.remote.search.SearchOperation;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserQueryImpl implements UserQuery {

  @Resource
  private UserRepo userRepo;

  @Resource
  private UserSearchRepo userSearchRepo;

  @Resource
  private AuthorizationQuery authorizationQuery;

  @Resource
  private DepartmentUserQuery departmentUserQuery;

  @Resource
  private GroupUserQuery groupUserQuery;

  @Resource
  private DepartmentRepo departmentRepo;

  @Resource
  private RoleQuery roleQuery;

  @Resource
  private ApplicationInfo applicationInfo;

  @Resource
  private UserInviteRepo userInviteRepo;

  @Resource
  private LoginHistoryRepo loginHistoryRepo;

  @Resource
  private ApplicationQuery applicationQuery;

  @Override
  public User findAndCheck(Long id) {
    return new BizTemplate<User>() {
      @Override
      protected User process() {
        return userRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("用户「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public List<User> findAndCheck(Collection<Long> ids) {
    return new BizTemplate<List<User>>() {
      @Override
      protected List<User> process() {
        if (isEmpty(ids)) {
          return new ArrayList<>();
        }

        // 查询所有用户
        List<User> users = userRepo.findAllById(ids);

        // 检查结果是否为空
        if (users.isEmpty()) {
          throw ResourceNotFound.of("用户「{0}」不存在", new Object[]{ids.iterator().next()});
        }

        // 检查是否有缺失的用户ID
        if (ids.size() != users.size()) {
          // 找出缺失的ID
          Set<Long> foundIds = users.stream()
              .map(User::getId)
              .collect(Collectors.toSet());
          List<Long> missingIds = ids.stream()
              .filter(id -> !foundIds.contains(id))
              .collect(Collectors.toList());

          if (!missingIds.isEmpty()) {
            throw ResourceNotFound.of("用户「{0}」不存在", new Object[]{missingIds.get(0)});
          }
        }
        return users;
      }
    }.execute();
  }

  @Override
  public Page<User> find(GenericSpecification<User> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<User>>() {
      @Override
      protected Page<User> process() {
        // 提取关联查询条件
        String roleIdStr = CriteriaUtils.findFirstValueAndRemove(
            spec.getCriteria(), "roleId");
        String departmentIdStr = CriteriaUtils.findFirstValueAndRemove(
            spec.getCriteria(), "departmentId");
        String groupIdStr = CriteriaUtils.findFirstValueAndRemove(
            spec.getCriteria(), "groupId");
        String appCodeStr = CriteriaUtils.findFirstValueAndRemove(
            spec.getCriteria(), "appCode");

        // 如果存在关联查询条件，根据关系表组装关联用户ID进行查询
        Set<Long> userIds = new LinkedHashSet<>();
        if (roleIdStr != null || departmentIdStr != null || groupIdStr != null
            || isNotEmpty(appCodeStr)) {
          // 根据角色ID查询用户ID
          if (roleIdStr != null) {
            Set<Long> roleUserIds = authorizationQuery.collectUserIdsByRoleId(parseLong(roleIdStr));
            if (roleUserIds.isEmpty()) {
              // 如果没有找到符合条件的用户，返回空结果
              return Page.empty(pageable);
            }
            userIds.addAll(roleUserIds);
          }

          // 根据部门ID查询用户ID
          if (departmentIdStr != null) {
            List<DepartmentUser> departmentUsers
                = departmentUserQuery.findByDepartmentId(parseLong(departmentIdStr));
            Set<Long> deptUserIds = departmentUsers.stream()
                .map(DepartmentUser::getUserId)
                .collect(Collectors.toSet());
            if (deptUserIds.isEmpty()) {
              // 如果没有找到符合条件的用户，返回空结果
              return Page.empty(pageable);
            }
            // 如果已有其他条件，取交集；否则直接添加
            if (userIds.isEmpty()) {
              userIds.addAll(deptUserIds);
            } else {
              userIds.retainAll(deptUserIds);
              if (userIds.isEmpty()) {
                // 交集为空，返回空结果
                return Page.empty(pageable);
              }
            }
          }

          // 根据组ID查询用户ID
          if (groupIdStr != null) {
            List<Long> groupUserIds = groupUserQuery.findUserIdsByGroupId(parseLong(groupIdStr));
            if (groupUserIds.isEmpty()) {
              // 如果没有找到符合条件的用户，返回空结果
              return Page.empty(pageable);
            }
            Set<Long> groupUserIdsSet = new HashSet<>(groupUserIds);
            // 如果已有其他条件，取交集；否则直接添加
            if (userIds.isEmpty()) {
              userIds.addAll(groupUserIdsSet);
            } else {
              userIds.retainAll(groupUserIdsSet);
              if (userIds.isEmpty()) {
                // 交集为空，返回空结果
                return Page.empty(pageable);
              }
            }
          }

          // 根据应用编码查询拥有该应用权限的用户ID
          // 注意：必须在userId收集完成后执行下面代码
          if (isNotEmpty(appCodeStr)) {
            // 如果应用有默认角色，则所有用户都为应用用户
            Role defaultRole = roleQuery.findByAppCodeAndIsDefaultTrue(appCodeStr,
                applicationInfo.getEditionType());
            if (defaultRole == null){
              // 没有默认橘色查询授权用户
              Set<Long> appUserIds = authorizationQuery.collectUserIdsByApplicationCode(appCodeStr);
              if (appUserIds.isEmpty()) {
                return Page.empty(pageable);
              }
              if (userIds.isEmpty()) {
                userIds.addAll(appUserIds);
              } else {
                // 注意：如果存在用户ID范围，必须要限制在授权的用户ID范围内
                userIds.retainAll(appUserIds);
                if (userIds.isEmpty()) {
                  return Page.empty(pageable);
                }
              }
            }
          }

          // 将用户ID添加到查询条件中
          if (!userIds.isEmpty()){
            spec.getCriteria().add(new SearchCriteria("id", userIds, SearchOperation.IN));
          }
        }

        return fullTextSearch
            ? userSearchRepo.find(spec.getCriteria(), pageable, User.class, match)
            : userRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public List<User> findAllByAccount(String account) {
    return new BizTemplate<List<User>>(false) {
      @Override
      protected List<User> process() {
        List<User> user = new ArrayList<>();

        // 判断账号类型并查询
        if (isEmail(account)) {
          // 邮箱查询
          List<User> emailUsers = userRepo.findAllByEmail(account);
          if (emailUsers != null && !emailUsers.isEmpty()) {
            user.addAll(emailUsers);
          }
        } else {
          // 手机号查询
          List<User> mobileUsers = userRepo.findAllByPhone(account);
          if (mobileUsers != null && !mobileUsers.isEmpty()) {
            user.addAll(mobileUsers);
          }
        }
        return user;
      }
    }.execute();
  }

  @Override
  public UserStatsVo getStats(String appCode) {
    return new BizTemplate<UserStatsVo>() {
      @Override
      protected UserStatsVo process() {
        UserStatsVo stats = new UserStatsVo();
        LocalDateTime now = LocalDateTime.now();
        Long optTenantId = getOptTenantId();

        // 应用编码过滤：获取该应用下的用户ID集合
        Set<Long> appUserIds = null;
        Long appIdForInvite = null;
        if (isNotEmpty(appCode)) {
          Role defaultRole = roleQuery.findByAppCodeAndIsDefaultTrue(appCode,
              applicationInfo.getEditionType());
          if (defaultRole == null) {
            appUserIds = authorizationQuery.collectUserIdsByApplicationCode(appCode);
            if (appUserIds.isEmpty()) {
              return buildEmptyStats();
            }
          }
          appIdForInvite = applicationQuery.findByCode(appCode).map(a -> a.getId()).orElse(null);
        }

        // 构建用户范围查询条件
        GenericSpecification<User> baseSpec = new GenericSpecification<>();
        if (appUserIds != null && !appUserIds.isEmpty()) {
          baseSpec.getCriteria().add(new SearchCriteria("id", appUserIds, SearchOperation.IN));
        }

        // 计算时间范围
        LocalDateTime firstDayOfMonth = now.with(TemporalAdjusters.firstDayOfMonth())
            .withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime firstDayOfLastMonth = firstDayOfMonth.minusMonths(1);
        LocalDateTime firstDayOfSixMonthsAgo = firstDayOfMonth.minusMonths(6);
        LocalDateTime sevenDaysAgo = now.minusDays(7);

        // 统计用户数据（按应用过滤）
        long totalUsers = appUserIds != null
            ? userRepo.count(baseSpec)
            : userRepo.count();
        stats.setTotalUsers(totalUsers);

        // 计算总用户数变化量（对比上个月）
        GenericSpecification<User> beforeMonthSpec = new GenericSpecification<>();
        beforeMonthSpec.getCriteria().add(
            new SearchCriteria("createdDate", firstDayOfMonth, SearchOperation.LESS_THAN));
        if (appUserIds != null) {
          beforeMonthSpec.getCriteria().add(new SearchCriteria("id", appUserIds, SearchOperation.IN));
        }
        long totalUsersLastMonth = userRepo.count(beforeMonthSpec);
        stats.setTotalUsersChange(totalUsers - totalUsersLastMonth);

        // 统计活跃用户
        GenericSpecification<User> activeSpec = new GenericSpecification<>();
        activeSpec.getCriteria().add(new SearchCriteria("status", UserStatus.ACTIVE, SearchOperation.EQUAL));
        if (appUserIds != null) {
          activeSpec.getCriteria().add(new SearchCriteria("id", appUserIds, SearchOperation.IN));
        }
        long activeUsers = userRepo.count(activeSpec);
        stats.setActiveUsers(activeUsers);

        // 计算活跃用户增长率（对比上个月）
        long newActiveUsersThisMonth = userRepo.count(
            buildCreatedAfterSpec(firstDayOfMonth, appUserIds));
        long activeUsersAtLastMonthEnd = Math.max(0, activeUsers - newActiveUsersThisMonth);
        if (activeUsersAtLastMonthEnd > 0) {
          double activeUsersGrowthRate =
              ((double) (activeUsers - activeUsersAtLastMonthEnd) / activeUsersAtLastMonthEnd)
                  * 100;
          stats.setActiveUsersGrowthRate(Math.round(activeUsersGrowthRate * 10.0) / 10.0);
        } else {
          stats.setActiveUsersGrowthRate(0.0);
        }

        GenericSpecification<User> disabledSpec = new GenericSpecification<>();
        disabledSpec.getCriteria().add(new SearchCriteria("status", UserStatus.DISABLED, SearchOperation.EQUAL));
        if (appUserIds != null) {
          disabledSpec.getCriteria().add(new SearchCriteria("id", appUserIds, SearchOperation.IN));
        }
        stats.setDisabledUsers(userRepo.count(disabledSpec));

        GenericSpecification<User> pendingSpec = new GenericSpecification<>();
        pendingSpec.getCriteria().add(new SearchCriteria("status", UserStatus.PENDING, SearchOperation.EQUAL));
        if (appUserIds != null) {
          pendingSpec.getCriteria().add(new SearchCriteria("id", appUserIds, SearchOperation.IN));
        }
        stats.setPendingUsers(userRepo.count(pendingSpec));

        // 待接收邀请数
        long pendingInvites = appIdForInvite != null
            ? userInviteRepo.countByStatusAndAppId(InviteStatus.PENDING, appIdForInvite)
            : userInviteRepo.countByStatus(InviteStatus.PENDING);
        stats.setPendingInvites(pendingInvites);

        // 过去7天活跃率
        long activeUsers7Days = appUserIds != null && !appUserIds.isEmpty()
            ? loginHistoryRepo.countDistinctUserIdByLoginTimeAfterAndUserIdIn(sevenDaysAgo, appUserIds)
            : loginHistoryRepo.countDistinctUserIdByLoginTimeAfter(sevenDaysAgo);
        double activeRate7Days = totalUsers > 0
            ? Math.round((activeUsers7Days * 100.0 / totalUsers) * 10.0) / 10.0
            : 0.0;
        stats.setActiveRate7Days(activeRate7Days);

        // 统计管理员数量
        long adminUsers = userRepo.countSysAdminUsers(optTenantId);
        stats.setAdminUsers(adminUsers);

        // 计算管理员数量变化量（对比上个月）
        long adminUsersLastMonth = userRepo.countSysAdminUsersByCreatedDateBefore(optTenantId,
            firstDayOfMonth);
        stats.setAdminUsersChange(adminUsers - adminUsersLastMonth);

        // 统计本月新增用户
        long newUsersThisMonth = userRepo.count(buildCreatedAfterSpec(firstDayOfMonth, appUserIds));
        stats.setNewUsersThisMonth(newUsersThisMonth);

        // 计算本月新增用户增长率（对比上个月同期）
        long newUsersLastMonth = userRepo.count(buildCreatedAfterSpec(firstDayOfLastMonth, appUserIds))
            - userRepo.count(buildCreatedBeforeSpec(firstDayOfMonth, appUserIds));
        if (newUsersLastMonth > 0) {
          double newUsersGrowthRate =
              ((double) (newUsersThisMonth - newUsersLastMonth) / newUsersLastMonth) * 100;
          stats.setNewUsersGrowthRate(Math.round(newUsersGrowthRate * 10.0) / 10.0);
        } else {
          stats.setNewUsersGrowthRate(newUsersThisMonth > 0 ? 100.0 : 0.0);
        }

        // 统计在线用户数量（使用SQL COUNT查询，性能更好）
        stats.setOnlineUsers(userRepo.countOnlineUsers(optTenantId));

        // 统计部门人员分布
        List<Department> departments = departmentRepo.findAll();
        Map<Long, Long> departmentUserCountMap = new HashMap<>();
        if (!departments.isEmpty()) {
          List<Long> departmentIds = departments.stream().map(Department::getId)
              .collect(Collectors.toList());
          departmentUserCountMap = departmentUserQuery.countUsersByDepartmentIds(departmentIds);
        }

        Map<Long, Department> departmentMap = departments.stream()
            .collect(Collectors.toMap(Department::getId, d -> d));
        List<UserStatsVo.DepartmentDistributionVo> departmentDistribution = new ArrayList<>();
        long totalUsersInDepartments = 0L;
        for (Map.Entry<Long, Long> entry : departmentUserCountMap.entrySet()) {
          Department dept = departmentMap.get(entry.getKey());
          if (dept != null && entry.getValue() > 0) {
            UserStatsVo.DepartmentDistributionVo dist = new UserStatsVo.DepartmentDistributionVo();
            dist.setDepartmentName(dept.getName());
            dist.setUserCount(entry.getValue());
            departmentDistribution.add(dist);
            totalUsersInDepartments += entry.getValue();
          }
        }

        // 计算占比
        if (totalUsersInDepartments > 0) {
          for (UserStatsVo.DepartmentDistributionVo dist : departmentDistribution) {
            double percentage = ((double) dist.getUserCount() / totalUsersInDepartments) * 100;
            dist.setPercentage(Math.round(percentage * 10.0) / 10.0);
          }
        }

        // 按用户数降序排序
        departmentDistribution.sort((a, b) -> Long.compare(b.getUserCount(), a.getUserCount()));
        stats.setDepartmentDistribution(departmentDistribution);

        // 统计用户增长趋势（过去6个月）
        List<Object[]> monthlyCounts = userRepo.countUsersByMonth(optTenantId,
            firstDayOfSixMonthsAgo);
        Map<String, Long> monthlyCountMap = new HashMap<>();
        for (Object[] result : monthlyCounts) {
          int year = ((Number) result[0]).intValue();
          int month = ((Number) result[1]).intValue();
          long count = ((Number) result[2]).longValue();
          String monthKey = year + "-" + String.format("%02d", month);
          monthlyCountMap.put(monthKey, count);
        }

        // 生成过去6个月的数据：累计用户数 = 6个月前已有的用户数 + 各月新增用户
        // 注意：totalUsersLastMonth 已包含过去所有月份的新增，不能作为基数再累加 monthlyCounts，否则会重复计算
        List<UserStatsVo.UserGrowthTrendVo> growthTrend = new ArrayList<>();
        long cumulativeCount = userRepo.count(buildCreatedBeforeSpec(
            firstDayOfSixMonthsAgo, appUserIds)); // 6个月窗口前的用户基数
        for (int i = 5; i >= 0; i--) {
          LocalDateTime monthStart = firstDayOfMonth.minusMonths(i);
          int year = monthStart.getYear();
          int month = monthStart.getMonthValue();
          String monthKey = year + "-" + String.format("%02d", month);
          long monthNewUsers = monthlyCountMap.getOrDefault(monthKey, 0L);
          cumulativeCount += monthNewUsers;

          UserStatsVo.UserGrowthTrendVo trend = new UserStatsVo.UserGrowthTrendVo();
          trend.setMonth(month + "月");
          trend.setTotalCount(cumulativeCount);
          growthTrend.add(trend);
        }
        stats.setGrowthTrend(growthTrend);

        return stats;
      }

      private UserStatsVo buildEmptyStats() {
        UserStatsVo empty = new UserStatsVo();
        empty.setTotalUsers(0L);
        empty.setTotalUsersChange(0L);
        empty.setActiveUsers(0L);
        empty.setActiveUsersGrowthRate(0.0);
        empty.setDisabledUsers(0L);
        empty.setPendingUsers(0L);
        empty.setPendingInvites(0L);
        empty.setActiveRate7Days(0.0);
        empty.setAdminUsers(0L);
        empty.setAdminUsersChange(0L);
        empty.setNewUsersThisMonth(0L);
        empty.setNewUsersGrowthRate(0.0);
        empty.setOnlineUsers(0L);
        empty.setDepartmentDistribution(Collections.emptyList());
        empty.setGrowthTrend(Collections.emptyList());
        return empty;
      }

      private GenericSpecification<User> buildCreatedAfterSpec(LocalDateTime after, Set<Long> userIds) {
        GenericSpecification<User> spec = new GenericSpecification<>();
        spec.getCriteria().add(new SearchCriteria("createdDate", after, SearchOperation.GREATER_THAN_EQUAL));
        if (userIds != null && !userIds.isEmpty()) {
          spec.getCriteria().add(new SearchCriteria("id", userIds, SearchOperation.IN));
        }
        return spec;
      }

      private GenericSpecification<User> buildCreatedBeforeSpec(LocalDateTime before, Set<Long> userIds) {
        GenericSpecification<User> spec = new GenericSpecification<>();
        spec.getCriteria().add(new SearchCriteria("createdDate", before, SearchOperation.LESS_THAN));
        if (userIds != null && !userIds.isEmpty()) {
          spec.getCriteria().add(new SearchCriteria("id", userIds, SearchOperation.IN));
        }
        return spec;
      }
    }.execute();
  }

  @Override
  public User findByEmail(String email) {
    return userRepo.findByEmail(email);
  }

  @Override
  public Long findTenantIdByEmail(String email) {
    return userRepo.findTenantIdByEmail(email);
  }

  @Override
  public Long findTenantIdByPhone(String phone) {
    return userRepo.findTenantIdByPhone(phone);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userRepo.existsByEmail(email);
  }

  @Override
  public boolean existsByPhone(String phone) {
    return userRepo.existsByPhone(phone);
  }

  @Override
  public boolean existsByUsername(String username) {
    boolean multiTenantCtrl = PrincipalContextUtils.isMultiTenantCtrl();
    boolean exists = false;
    if (multiTenantCtrl) {
      PrincipalContextUtils.setMultiTenantCtrl(false);
      exists = userRepo.existsByUsername(username);
      PrincipalContextUtils.setMultiTenantCtrl(true);
    }
    return exists;
  }

  @Override
  public boolean existsByUsernameAndIdNot(String username, Long id) {
    boolean multiTenantCtrl = PrincipalContextUtils.isMultiTenantCtrl();
    boolean exists = false;
    if (multiTenantCtrl) {
      PrincipalContextUtils.setMultiTenantCtrl(false);
      exists = userRepo.existsByUsernameAndIdNot(username, id);
      PrincipalContextUtils.setMultiTenantCtrl(true);
    }
    return exists;
  }

  @Override
  public long count() {
    return userRepo.count();
  }

  @Override
  public long countByStatus(UserStatus status) {
    return userRepo.countByStatus(status);
  }

  @Override
  public long countByTenantId(Long tenantId) {
    return userRepo.countByTenantId(tenantId);
  }

}
