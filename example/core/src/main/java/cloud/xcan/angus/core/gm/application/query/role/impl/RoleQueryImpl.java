package cloud.xcan.angus.core.gm.application.query.role.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.api.commonlink.department.Department;
import cloud.xcan.angus.api.commonlink.department.DepartmentRepo;
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.group.GroupRepo;
import cloud.xcan.angus.api.commonlink.role.Role;
import cloud.xcan.angus.api.commonlink.role.RoleRepo;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.application.query.authorization.AuthorizationQuery;
import cloud.xcan.angus.core.gm.application.query.role.RoleQuery;
import cloud.xcan.angus.core.gm.domain.authorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationRepo;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationRole;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationRoleRepo;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import cloud.xcan.angus.core.gm.domain.role.RoleSearchRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.utils.PrincipalContextUtils;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.spec.utils.ObjectUtils;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class RoleQueryImpl implements RoleQuery {

  @Resource
  private RoleRepo roleRepo;

  @Resource
  private RoleSearchRepo roleSearchRepo;

  @Resource
  private AuthorizationQuery authorizationQuery;

  @Resource
  private AuthorizationRoleRepo authorizationRoleRepo;

  @Resource
  private AuthorizationRepo authorizationRepo;

  @Resource
  private UserRepo userRepo;

  @Resource
  private DepartmentRepo departmentRepo;

  @Resource
  private GroupRepo groupRepo;

  @Resource
  private ApplicationQuery applicationQuery;

  @Resource
  private UserManager userManager;

  @Override
  public Role findAndCheck(Long id) {
    return new BizTemplate<Role>(false) {
      @Override
      protected Role process() {
        Role role = roleRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("角色「{0}」不存在", new Object[]{id}));

        // 系统定义角色允许所有租户访问
        if (!role.getTenantId().equals(getOptTenantId()) && !role.getIsSystem()) {
          throw ResourceNotFound.of("角色「{0}」不存在", new Object[]{id});
        }
        return role;
      }
    }.execute();
  }

  @Override
  public List<Role> findAndCheck(Collection<Long> ids) {
    return new BizTemplate<List<Role>>(false) {
      @Override
      protected List<Role> process() {
        if (isEmpty(ids)) {
          return new ArrayList<>();
        }

        List<Role> roles = roleRepo.findAllById(ids);
        assertResourceNotFound(ObjectUtils.isNotEmpty(roles), ids.iterator().next(), "Role");
        if (ids.size() != roles.size()) {
          for (Role role : roles) {
            assertResourceNotFound(ids.contains(role.getId()), role.getId(), "Role");
            // 系统定义角色允许所有租户访问
            if (!role.getTenantId().equals(getOptTenantId()) && !role.getIsSystem()) {
              throw ResourceNotFound.of("角色「{0}」不存在", new Object[]{role.getId()});
            }
          }
        }
        return roles;
      }
    }.execute();
  }

  @Override
  public Page<Role> find(GenericSpecification<Role> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Role>>(false) {
      @Override
      protected Page<Role> process() {
        List<Long> roleIds = findWideTenantRoles(getOptTenantId())
            .stream().map(Role::getId)
            .toList();
        if (isEmpty(roleIds)) {
          return Page.empty(pageable);
        }
        spec.getCriteria().add(SearchCriteria.in("id", roleIds));
        return fullTextSearch
            ? roleSearchRepo.find(spec.getCriteria(), pageable, Role.class, match)
            : roleRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public Page<User> findWideUsersByRoleId(Long roleId, String name, PageRequest pageable) {
    return new BizTemplate<Page<User>>() {
      @Override
      protected Page<User> process() {
        // 通过授权角色关系表查找所有包含该角色的授权
        List<AuthorizationRole> authorizationRoles = authorizationRoleRepo.findByRoleId(roleId);
        if (authorizationRoles.isEmpty()) {
          return Page.empty(pageable);
        }

        // 获取所有授权ID
        List<Long> authorizationIds = authorizationRoles.stream()
            .map(AuthorizationRole::getAuthorizationId)
            .distinct().collect(Collectors.toList());

        // 收集所有用户ID（包括直接授权、部门授权、组授权）
        Set<Long> userIdSet = authorizationQuery.collectWideUserIdsFromAuthorizations(
            authorizationIds);

        if (userIdSet.isEmpty()) {
          return Page.empty(pageable);
        }

        // 转换为列表并分页
        List<Long> userIds = new ArrayList<>(userIdSet);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), userIds.size());
        List<Long> pagedUserIds = userIds.subList(start, end);

        // 查询用户
        List<User> users = userRepo.findAllById(pagedUserIds);
        if (isNotEmpty(name)) {
          users = users.stream()
              .filter(user -> user.getName() != null && user.getName().contains(name))
              .collect(Collectors.toList());
        }
        // 创建分页结果
        return new PageImpl<>(users, pageable, userIdSet.size());
      }
    }.execute();
  }

  @Override
  public Page<User> findUsersByRoleId(Long roleId, String name, PageRequest pageable) {
    return new BizTemplate<Page<User>>() {
      @Override
      protected Page<User> process() {
        // 通过授权角色关系表查找所有包含该角色的授权
        List<AuthorizationRole> authorizationRoles = authorizationRoleRepo.findByRoleId(roleId);
        if (authorizationRoles.isEmpty()) {
          return Page.empty(pageable);
        }

        // 获取所有授权ID
        List<Long> authorizationIds = authorizationRoles.stream()
            .map(AuthorizationRole::getAuthorizationId)
            .distinct().collect(Collectors.toList());

        // 查询所有授权
        List<Authorization> authorizations = authorizationRepo.findAllById(authorizationIds);

        // 只收集直接用户授权的用户ID（不包含部门和组的关联用户）
        List<Long> userIds = authorizations.stream()
            .filter(auth -> auth.getSubjectType() == AuthorizationSubjectType.USER)
            .map(Authorization::getSubjectId)
            .distinct().collect(Collectors.toList());

        if (userIds.isEmpty()) {
          return Page.empty(pageable);
        }

        // 转换为列表并分页
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), userIds.size());
        List<Long> pagedUserIds = userIds.subList(start, end);

        // 查询用户
        List<User> users = userRepo.findAllById(pagedUserIds);
        if (isNotEmpty(name)) {
          users = users.stream()
              .filter(user -> user.getName() != null && user.getName().contains(name))
              .collect(Collectors.toList());
        }

        // 创建分页结果
        return new PageImpl<>(users, pageable, userIds.size());
      }
    }.execute();
  }

  @Override
  public Page<Department> findDepartmentsByRoleId(Long roleId, String name, PageRequest pageable) {
    return new BizTemplate<Page<Department>>() {
      @Override
      protected Page<Department> process() {
        // 通过授权角色关系表查找所有包含该角色的授权
        List<AuthorizationRole> authorizationRoles = authorizationRoleRepo.findByRoleId(roleId);
        if (authorizationRoles.isEmpty()) {
          return Page.empty(pageable);
        }

        // 获取所有授权ID
        List<Long> authorizationIds = authorizationRoles.stream()
            .map(AuthorizationRole::getAuthorizationId)
            .distinct().collect(Collectors.toList());

        // 查询所有授权
        List<Authorization> authorizations = authorizationRepo.findAllById(authorizationIds);

        // 收集部门ID（只包含部门类型的授权）
        List<Long> departmentIds = authorizations.stream()
            .filter(auth -> auth.getSubjectType() == AuthorizationSubjectType.DEPARTMENT)
            .map(Authorization::getSubjectId)
            .distinct().collect(Collectors.toList());

        if (departmentIds.isEmpty()) {
          return Page.empty(pageable);
        }

        // 查询部门
        List<Department> departments = departmentRepo.findAllById(departmentIds);
        if (isNotEmpty(name)) {
          departments = departments.stream()
              .filter(dept -> dept.getName() != null && dept.getName().contains(name))
              .collect(Collectors.toList());
        }

        // 转换为列表并分页
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), departments.size());
        List<Department> pagedDepartments = departments.subList(start, end);

        // 创建分页结果
        return new PageImpl<>(pagedDepartments, pageable, departments.size());
      }
    }.execute();
  }

  @Override
  public Page<Group> findGroupsByRoleId(Long roleId, String name, PageRequest pageable) {
    return new BizTemplate<Page<Group>>() {
      @Override
      protected Page<Group> process() {
        // 通过授权角色关系表查找所有包含该角色的授权
        List<AuthorizationRole> authorizationRoles = authorizationRoleRepo.findByRoleId(roleId);
        if (authorizationRoles.isEmpty()) {
          return Page.empty(pageable);
        }

        // 获取所有授权ID
        List<Long> authorizationIds = authorizationRoles.stream()
            .map(AuthorizationRole::getAuthorizationId)
            .distinct().collect(Collectors.toList());

        // 查询所有授权
        List<Authorization> authorizations = authorizationRepo.findAllById(authorizationIds);

        // 收集组ID（只包含组类型的授权）
        List<Long> groupIds = authorizations.stream()
            .filter(auth -> auth.getSubjectType() == AuthorizationSubjectType.GROUP)
            .map(Authorization::getSubjectId)
            .distinct().collect(Collectors.toList());

        if (groupIds.isEmpty()) {
          return Page.empty(pageable);
        }

        // 查询组
        List<Group> groups = groupRepo.findAllById(groupIds);
        if (isNotEmpty(name)) {
          groups = groups.stream()
              .filter(group -> group.getName() != null && group.getName().contains(name))
              .collect(Collectors.toList());
        }

        // 转换为列表并分页
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), groups.size());
        List<Group> pagedGroups = groups.subList(start, end);

        // 创建分页结果
        return new PageImpl<>(pagedGroups, pageable, groups.size());
      }
    }.execute();
  }

  @Override
  public List<Role> findWideRolesByUserId(Long userId) {
    return new BizTemplate<List<Role>>(false) {
      @Override
      protected List<Role> process() {
        Set<Long> roleIds = new LinkedHashSet<>();

        // 1. 通过授权链查询角色：用户、部门、组的授权
        Set<Long> subjectIds = new HashSet<>();
        subjectIds.add(userId);
        subjectIds.addAll(userManager.findValidOrgIdsById(userId));

        List<Long> authorizationIds = authorizationRepo.findAuthorizationIdsBySubjectIdInAndStatus(
            subjectIds, EnabledStatus.ENABLED);
        if (isNotEmpty(authorizationIds)) {
          List<AuthorizationRole> authorizationRoles =
              authorizationRoleRepo.findByAuthorizationIdIn(authorizationIds);
          authorizationRoles.stream()
              .map(AuthorizationRole::getRoleId)
              .forEach(roleIds::add);
        }

        // 2. 应用默认角色（isDefault=true）针对所有用户自动生效，逻辑上属于用户
        List<Role> defaultRoles =
            roleRepo.findByIsDefaultTrueAndStatus(EnabledStatus.ENABLED);
        if (isNotEmpty(defaultRoles)) {
          defaultRoles.stream().map(Role::getId).filter(id -> id != null).forEach(roleIds::add);
        }

        if (roleIds.isEmpty()) {
          return new ArrayList<>();
        }

        return roleRepo.findAllById(new ArrayList<>(roleIds));
      }
    }.execute();
  }

  @Override
  public long countRolesByApplicationId(Long appId) {
    return new BizTemplate<Long>(false) {
      @Override
      protected Long process() {
        return roleRepo.countByAppId(appId);
      }
    }.execute();
  }

  @Override
  public List<Role> findByAppId(Long appId) {
    boolean multiTenantCtrl = PrincipalContextUtils.isMultiTenantCtrl();
    try {
      PrincipalContextUtils.setMultiTenantCtrl(false);
      return roleRepo.findWideRolesByAppIdAndTenantId(appId, getOptTenantId());
    } finally {
      if (multiTenantCtrl) {
        PrincipalContextUtils.setMultiTenantCtrl(true);
      }
    }
  }

  @Override
  public Role findByAppCodeAndIsDefaultTrue(String appCodeStr, String editionType) {
    boolean multiTenantCtrl = PrincipalContextUtils.isMultiTenantCtrl();
    try {
      PrincipalContextUtils.setMultiTenantCtrl(false);
      Application application = applicationQuery.findByCodeAndEditionType(appCodeStr, editionType)
          .orElseThrow(() -> ResourceNotFound.of("应用「{0}-{1}」不存在",
              new Object[]{appCodeStr, editionType}));
      return roleRepo.findByAppIdAndIsDefaultTrue(application.getId());
    } finally {
      if (multiTenantCtrl) {
        PrincipalContextUtils.setMultiTenantCtrl(true);
      }
    }
  }

  @Override
  public Role findByAppIdAndIsDefaultTrue(Long appId) {
    boolean multiTenantCtrl = PrincipalContextUtils.isMultiTenantCtrl();
    try {
      PrincipalContextUtils.setMultiTenantCtrl(false);
      return roleRepo.findByAppIdAndIsDefaultTrue(appId);
    } finally {
      if (multiTenantCtrl) {
        PrincipalContextUtils.setMultiTenantCtrl(true);
      }
    }
  }

  @Override
  public List<Role> findAllById(Set<Long> roleIds) {
    boolean multiTenantCtrl = PrincipalContextUtils.isMultiTenantCtrl();
    try {
      PrincipalContextUtils.setMultiTenantCtrl(false);
      return roleRepo.findAllById(roleIds);
    } finally {
      if (multiTenantCtrl) {
        PrincipalContextUtils.setMultiTenantCtrl(true);
      }
    }
  }

  @Override
  public List<Role> findWideTenantRoles(Long tenantId) {
    return roleRepo.findWideRolesByTenantId(tenantId);
  }

  @Override
  public List<User> findUsersByRoleId(Long roleId) {
    return authorizationQuery.findUsersByRoleId(roleId);
  }

  @Override
  public long countTotal() {
    return roleRepo.count();
  }

  @Override
  public long countUsersByRoleId(Long roleId) {
    return authorizationQuery.countUsersByRoleId(roleId);
  }

  @Override
  public Map<Long, Long> countUsersByRoleIds(Collection<Long> roleIds) {
    return authorizationQuery.countUsersByRoleIds(roleIds);
  }

  @Override
  public long countTotalUsers() {
    return authorizationQuery.countTotalUsers();
  }

  @Override
  public long countSystemRoles() {
    return roleRepo.countByIsSystemTrue();
  }

  @Override
  public long countCustomRoles() {
    return roleRepo.countByIsSystemFalse();
  }

}
