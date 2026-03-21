package cloud.xcan.angus.core.gm.application.query.authorization.impl;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.api.commonlink.department.DepartmentUserRepo;
import cloud.xcan.angus.api.commonlink.group.GroupUserRepo;
import cloud.xcan.angus.api.commonlink.role.Role;
import cloud.xcan.angus.api.commonlink.role.RoleRepo;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.application.query.authorization.AuthorizationQuery;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentQuery;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.application.query.role.RoleQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.authorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationRepo;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationRole;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationRoleRepo;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationSearchRepo;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationStatsVo;
import cloud.xcan.angus.core.jpa.criteria.CriteriaUtils;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationQueryImpl implements AuthorizationQuery {

  @Resource
  private AuthorizationRepo authorizationRepo;

  @Resource
  private AuthorizationRoleRepo authorizationRoleRepo;

  @Resource
  private AuthorizationSearchRepo authorizationSearchRepo;

  @Resource
  private ApplicationQuery applicationQuery;

  @Resource
  private RoleQuery roleQuery;

  @Resource
  private UserQuery userQuery;

  @Resource
  private DepartmentQuery departmentQuery;

  @Resource
  private GroupQuery groupQuery;

  @Resource
  private DepartmentUserRepo departmentUserRepo;

  @Resource
  private GroupUserRepo groupUserRepo;

  @Resource
  private UserRepo userRepo;

  @Override
  public Authorization findAndCheck(Long id) {
    return new BizTemplate<Authorization>() {
      @Override
      protected Authorization process() {
        return authorizationRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("授权「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public Authorization getSubjectAuthorization(AuthorizationSubjectType subjectType,
      Long subjectId) {
    return new BizTemplate<Authorization>() {
      @Override
      protected Authorization process() {
        return authorizationRepo.findBySubjectTypeAndSubjectId(subjectType, subjectId).orElse(null);
      }
    }.execute();
  }

  @Override
  public Page<Authorization> find(GenericSpecification<Authorization> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Authorization>>() {
      @Override
      protected Page<Authorization> process() {
        // 提取 appId 和 roleId 条件
        String appId = CriteriaUtils.findFirstValueAndRemove(spec.getCriteria(), "appId");
        String roleId = CriteriaUtils.findFirstValueAndRemove(spec.getCriteria(), "roleId");

        // 如果存在 appId 或 roleId 条件，进行关联查询
        Set<Long> authorizationIds;
        if (appId != null || roleId != null) {
          Long appId0 = appId != null ? Long.parseLong(appId) : null;
          Long roleId0 = roleId != null ? Long.parseLong(roleId) : null;
          authorizationIds = findAuthorizationIdsByAppIdAndRoleId(appId0, roleId0);
          if (authorizationIds.isEmpty()) {
            // 如果没有找到符合条件的授权，返回空结果
            return Page.empty(pageable);
          }
          // 将 authorizationIds 添加到查询条件中
          spec.getCriteria().add(SearchCriteria.in("id", new ArrayList<>(authorizationIds)));
        }

        return fullTextSearch
            ? authorizationSearchRepo.find(spec.getCriteria(), pageable, Authorization.class, match)
            : authorizationRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public AuthorizationStatsVo getStats() {
    return new BizTemplate<AuthorizationStatsVo>() {
      @Override
      protected AuthorizationStatsVo process() {
        AuthorizationStatsVo vo = new AuthorizationStatsVo();
        List<Object[]> results = authorizationRepo.countGroupBySubjectType();
        long total = 0;
        long userCount = 0;
        long departmentCount = 0;
        long groupCount = 0;

        for (Object[] result : results) {
          AuthorizationSubjectType subjectType = AuthorizationSubjectType.valueOf(
              (String) result[0]);
          long count = ((Number) result[1]).longValue();
          total += count;

          if (AuthorizationSubjectType.USER.equals(subjectType)) {
            userCount = count;
          } else if (AuthorizationSubjectType.DEPARTMENT.equals(subjectType)) {
            departmentCount = count;
          } else if (AuthorizationSubjectType.GROUP.equals(subjectType)) {
            groupCount = count;
          }
        }

        vo.setTotalAuthorizations(total);
        vo.setUserAuthorizations(userCount);
        vo.setDepartmentAuthorizations(departmentCount);
        vo.setGroupAuthorizations(groupCount);
        return vo;
      }
    }.execute();
  }

  @Override
  public long countWideUsersByApplicationId(Long appId) {
    return new BizTemplate<Long>(false) {
      @Override
      protected Long process() {
        Set<Long> userIds = collectWideUserIdsByApplicationId(appId);
        return (long) userIds.size();
      }
    }.execute();
  }

  @Override
  public Set<Long> collectWideUserIdsByApplicationId(Long appId) {
    // 如果应用有默认角色，则所有用户都为应用用户
    Role defaultRole = roleQuery.findByAppIdAndIsDefaultTrue(appId);
    if (defaultRole != null) {
      return new TreeSet<>(userRepo.findAllIds());
    }
    Set<Long> authorizationIds = findAuthorizationIdsByAppIdAndRoleId(appId, null);
    if (authorizationIds.isEmpty()) {
      return new TreeSet<>();
    }
    return collectWideUserIdsFromAuthorizations(new ArrayList<>(authorizationIds));
  }

  @Override
  public Set<Long> collectUserIdsByApplicationCode(String appCode) {
    if (isEmpty(appCode)) {
      return new TreeSet<>();
    }
    Application application = applicationQuery.findByCode(appCode)
        .orElseThrow(() -> ResourceNotFound.of("应用「{0}」不存在", new Object[]{appCode}));
    return collectWideUserIdsByApplicationId(application.getId());
  }

  @Override
  public Authorization findById(Long id) {
    return authorizationRepo.findById(id).orElse(null);
  }

  @Override
  public Page<Authorization> findByStatus(EnabledStatus status, Pageable pageable) {
    return authorizationRepo.findByStatus(status, pageable);
  }

  @Override
  public long count() {
    return authorizationRepo.count();
  }

  @Override
  public long countByStatus(EnabledStatus status) {
    return authorizationRepo.countByStatus(status);
  }

  @Override
  public long countBySubjectType(AuthorizationSubjectType subjectType) {
    return authorizationRepo.countBySubjectType(subjectType);
  }

  @Override
  public String checkSubjectExists(AuthorizationSubjectType subjectType, Long subjectId) {
    if (subjectType == null) {
      throw new IllegalArgumentException("授权主体类型不能为空");
    }
    if (subjectId == null) {
      throw new IllegalArgumentException("授权主体ID不能为空");
    }

    return switch (subjectType) {
      case USER -> userQuery.findAndCheck(subjectId).getName();
      case DEPARTMENT -> departmentQuery.findAndCheck(subjectId).getName();
      case GROUP -> groupQuery.findAndCheck(subjectId).getName();
      default -> throw new IllegalArgumentException("不支持的授权主体类型: " + subjectType);
    };
  }

  @Override
  public long countByRoleId(Long roleId) {
    return authorizationRoleRepo.countByRoleId(roleId);
  }

  @Override
  public List<User> findUsersByRoleId(Long roleId) {
    Set<Long> userIdSet = collectUserIdsByRoleId(roleId);
    if (userIdSet.isEmpty()) {
      return new ArrayList<>();
    }
    return userRepo.findAllById(new ArrayList<>(userIdSet));
  }

  @Override
  public long countUsersByRoleId(Long roleId) {
    Set<Long> userIdSet = collectUserIdsByRoleId(roleId);
    return userIdSet.size();
  }

  @Override
  public long countTotalUsers() {
    Set<Long> userIdSet = collectAllUserIds();
    return userIdSet.size();
  }

  /**
   * 根据角色ID收集所有用户ID（包括直接授权、部门授权、组授权）
   */
  @Override
  public Set<Long> collectUserIdsByRoleId(Long roleId) {
    // 通过授权角色关系表查找所有包含该角色的授权
    List<AuthorizationRole> authorizationRoles = authorizationRoleRepo.findByRoleId(roleId);
    if (authorizationRoles.isEmpty()) {
      return new LinkedHashSet<>();
    }

    // 获取所有授权ID
    List<Long> authorizationIds = authorizationRoles.stream()
        .map(AuthorizationRole::getAuthorizationId)
        .distinct()
        .collect(Collectors.toList());

    // 查询所有授权并收集用户ID
    return collectWideUserIdsFromAuthorizations(authorizationIds);
  }

  /**
   * 收集所有授权中的用户ID（包括直接授权、部门授权、组授权）
   */
  private Set<Long> collectAllUserIds() {
    // 获取所有授权角色关系
    List<AuthorizationRole> authorizationRoles = authorizationRoleRepo.findAll();
    if (authorizationRoles.isEmpty()) {
      return new LinkedHashSet<>();
    }

    // 获取所有授权ID
    List<Long> authorizationIds = authorizationRoles.stream()
        .map(AuthorizationRole::getAuthorizationId)
        .distinct()
        .collect(Collectors.toList());

    // 查询所有授权并收集用户ID
    return collectWideUserIdsFromAuthorizations(authorizationIds);
  }

  @Override
  public Map<Long, Long> countUsersByRoleIds(Collection<Long> roleIds) {
    if (isEmpty(roleIds)) {
      return new HashMap<>();
    }

    // 初始化结果Map，确保所有角色都有0值
    Map<Long, Long> result = new HashMap<>();
    for (Long roleId : roleIds) {
      result.put(roleId, 0L);
    }

    // 批量查询所有角色ID对应的授权角色关系
    List<AuthorizationRole> authorizationRoles = authorizationRoleRepo.findByRoleIdIn(
        new ArrayList<>(roleIds));
    if (authorizationRoles.isEmpty()) {
      return result;
    }

    // 按角色ID分组授权角色关系
    Map<Long, List<AuthorizationRole>> roleToAuthRolesMap = authorizationRoles.stream()
        .collect(Collectors.groupingBy(AuthorizationRole::getRoleId));

    // 为每个角色收集用户ID并统计数量
    for (Map.Entry<Long, List<AuthorizationRole>> entry : roleToAuthRolesMap.entrySet()) {
      Long roleId = entry.getKey();
      List<AuthorizationRole> authRoles = entry.getValue();

      // 获取该角色的所有授权ID
      List<Long> authorizationIds = authRoles.stream()
          .map(AuthorizationRole::getAuthorizationId)
          .distinct()
          .collect(Collectors.toList());

      // 收集用户ID并统计数量
      Set<Long> userIdSet = collectWideUserIdsFromAuthorizations(authorizationIds);
      result.put(roleId, (long) userIdSet.size());
    }

    return result;
  }

  @Override
  public Set<Long> collectWideUserIdsFromAuthorizations(List<Long> authorizationIds) {
    // 查询所有授权
    List<Authorization> authorizations = authorizationRepo.findAllById(authorizationIds);

    // 收集所有用户ID（包括直接授权、部门授权、组授权）
    Set<Long> userIdSet = new LinkedHashSet<>();

    // 收集部门ID和组ID，用于批量查询
    Set<Long> departmentIds = new HashSet<>();
    Set<Long> groupIds = new HashSet<>();

    for (Authorization auth : authorizations) {
      if (auth.getSubjectType() == AuthorizationSubjectType.USER) {
        // 直接用户授权
        userIdSet.add(auth.getSubjectId());
      } else if (auth.getSubjectType() == AuthorizationSubjectType.DEPARTMENT) {
        // 收集部门ID，后续批量查询
        departmentIds.add(auth.getSubjectId());
      } else if (auth.getSubjectType() == AuthorizationSubjectType.GROUP) {
        // 收集组ID，后续批量查询
        groupIds.add(auth.getSubjectId());
      }
    }

    // 批量查询部门用户
    if (!departmentIds.isEmpty()) {
      Set<Long> deptUserIds = departmentUserRepo.findUserIdsByDeptIds(departmentIds);
      userIdSet.addAll(deptUserIds);
    }

    // 批量查询组用户
    if (!groupIds.isEmpty()) {
      Set<Long> groupUserIds = groupUserRepo.findUserIdsByGroupIds(groupIds);
      userIdSet.addAll(groupUserIds);
    }

    // 返回按 ID 升序排序的结果
    return new TreeSet<>(userIdSet);
  }

  /**
   * 根据 appId 和 roleId 查找符合条件的授权ID列表
   */
  private Set<Long> findAuthorizationIdsByAppIdAndRoleId(Long appId, Long roleId) {
    // 如果指定了 roleId，通过授权角色关系表查找
    if (roleId != null) {
      List<AuthorizationRole> authorizationRoles = authorizationRoleRepo.findByRoleId(roleId);
      Set<Long> authorizationIds = authorizationRoles.stream()
          .map(AuthorizationRole::getAuthorizationId)
          .collect(Collectors.toSet());

      // 如果只指定了 roleId，直接返回
      if (appId == null) {
        return authorizationIds;
      }

      // 查找该应用下的所有角色
      List<Role> roles = roleQuery.findByAppId(appId);
      if (roles.isEmpty()) {
        return new HashSet<>();
      }

      Set<Long> appRoleIds = roles.stream().map(Role::getId).collect(Collectors.toSet());

      // 验证 roleId 是否在该应用的角色列表中
      if (!appRoleIds.contains(roleId)) {
        // 如果指定的 roleId 不属于该应用，返回空结果
        return new HashSet<>();
      }

      // 查找这些角色的授权关系
      Set<Long> appAuthorizationIds
          = authorizationRoleRepo.findByRoleIdIn(new ArrayList<>(appRoleIds))
          .stream().map(AuthorizationRole::getAuthorizationId)
          .collect(Collectors.toSet());

      // 取交集
      authorizationIds.retainAll(appAuthorizationIds);
      return authorizationIds;
    }

    // 如果只指定了 appId，需要通过角色表查找
    if (appId != null) {
      // 查找该应用下的所有角色
      List<Role> roles = roleQuery.findByAppId(appId);
      if (roles.isEmpty()) {
        return new HashSet<>();
      }

      // 查找这些角色的授权关系
      Set<Long> appRoleIds = roles.stream().map(Role::getId).collect(Collectors.toSet());
      return authorizationRoleRepo.findByRoleIdIn(appRoleIds)
          .stream().map(AuthorizationRole::getAuthorizationId)
          .collect(Collectors.toSet());
    }
    return new HashSet<>();
  }

  @Override
  public void setRoleInfo(List<Authorization> authorizations) {
    if (isEmpty(authorizations)) {
      return;
    }
    Set<Long> authIds = authorizations.stream().map(Authorization::getId)
        .collect(Collectors.toSet());
    List<AuthorizationRole> authRoles = authorizationRoleRepo.findByAuthorizationIdIn(authIds);
    if (isEmpty(authRoles)) {
      return;
    }
    Set<Long> appRoleIds = authRoles.stream().map(AuthorizationRole::getRoleId)
        .collect(Collectors.toSet());
    List<Role> roles = roleQuery.findAllById(appRoleIds);
    if (isEmpty(roles)) {
      return;
    }
    Set<Long> appIds = roles.stream().map(Role::getAppId).collect(Collectors.toSet());
    Map<Long, Application> applicationMap = applicationQuery.findAllById(appIds).stream()
        .collect(Collectors.toMap(Application::getId, x -> x));
    for (Role role : roles) {
      role.setAppName(applicationMap.get(role.getAppId()) != null
          ? applicationMap.get(role.getAppId()).getName() : null);
    }

    for (Authorization authorization : authorizations) {
      authorization.setRoleInfos(
          authRoles.stream()
              .filter(ar -> ar.getAuthorizationId().equals(authorization.getId()))
              .map(ar -> roles.stream()
                  .filter(r -> r.getId().equals(ar.getRoleId()))
                  .findFirst().orElse(new Role()).toRoleInfo())
              .filter(Objects::nonNull)
              .collect(Collectors.toList())
      );
    }
  }

  /**
   * 批量计算授权记录对应的授权人数 性能优化：按主体类型分组，批量查询用户数，避免N+1查询问题
   */
  @Override
  public void setSubjectUserCounts(List<Authorization> authorizations) {
    // 按主体类型分组收集ID
    Set<Long> departmentIds = new HashSet<>();
    Set<Long> groupIds = new HashSet<>();

    for (Authorization authorization : authorizations) {
      AuthorizationSubjectType subjectType = authorization.getSubjectType();
      if (subjectType == null) {
        continue;
      }
      if (subjectType == AuthorizationSubjectType.USER) {
        // 用户类型授权人数固定为1
        authorization.setSubjectUserCount(1);
      } else if (subjectType == AuthorizationSubjectType.DEPARTMENT) {
        // 收集部门ID，后续批量查询
        departmentIds.add(authorization.getSubjectId());
      } else if (subjectType == AuthorizationSubjectType.GROUP) {
        // 收集组ID，后续批量查询
        groupIds.add(authorization.getSubjectId());
      }
    }

    // 批量查询部门用户数
    Map<Long, Long> departmentUserCountMap = new HashMap<>();
    if (!departmentIds.isEmpty()) {
      List<Object[]> deptCountResults = departmentUserRepo.countGroupByDepartmentIds(departmentIds);
      for (Object[] result : deptCountResults) {
        Long departmentId = ((Number) result[0]).longValue();
        Long userCount = ((Number) result[1]).longValue();
        departmentUserCountMap.put(departmentId, userCount);
      }
      // 对于没有用户的部门，设置用户数为0
      for (Long departmentId : departmentIds) {
        departmentUserCountMap.putIfAbsent(departmentId, 0L);
      }
    }

    // 批量查询组用户数
    Map<Long, Long> groupUserCountMap = new HashMap<>();
    if (!groupIds.isEmpty()) {
      List<Object[]> groupCountResults = groupUserRepo.countGroupByGroupIds(groupIds);
      for (Object[] result : groupCountResults) {
        Long groupId = ((Number) result[0]).longValue();
        Long userCount = ((Number) result[1]).longValue();
        groupUserCountMap.put(groupId, userCount);
      }
      // 对于没有用户的组，设置用户数为0
      for (Long groupId : groupIds) {
        groupUserCountMap.putIfAbsent(groupId, 0L);
      }
    }

    // 设置授权人数
    for (Authorization authorization : authorizations) {
      AuthorizationSubjectType subjectType = authorization.getSubjectType();
      if (subjectType == null) {
        continue;
      }
      if (subjectType == AuthorizationSubjectType.DEPARTMENT) {
        Long userCount = departmentUserCountMap.get(authorization.getSubjectId());
        authorization.setSubjectUserCount(userCount != null ? userCount.intValue() : 0);
      } else if (subjectType == AuthorizationSubjectType.GROUP) {
        Long userCount = groupUserCountMap.get(authorization.getSubjectId());
        authorization.setSubjectUserCount(userCount != null ? userCount.intValue() : 0);
      }
      // USER类型已经在上面设置为1了
    }
  }
}
