package cloud.xcan.angus.core.gm.interfaces.role.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.api.commonlink.application.ApplicationMenu;
import cloud.xcan.angus.api.commonlink.department.Department;
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.role.PermissionInfo;
import cloud.xcan.angus.api.commonlink.role.Role;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.api.gm.RolePermissionVo;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.role.RoleCmd;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationMenuQuery;
import cloud.xcan.angus.core.gm.application.query.authorization.AuthorizationAppQuery;
import cloud.xcan.angus.core.gm.application.query.role.RoleQuery;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import cloud.xcan.angus.core.gm.interfaces.application.facade.internal.assembler.ApplicationMenuAssembler;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationMenuVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.internal.assembler.DepartmentAssembler;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.internal.assembler.GroupAssembler;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.role.facade.RoleFacade;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RoleCreateDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RoleDefaultDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RoleFindDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RoleObjectFindDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RolePermissionUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RoleUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.internal.assembler.RoleAssembler;
import cloud.xcan.angus.core.gm.interfaces.role.facade.vo.AuthorizableApplicationMenuVo;
import cloud.xcan.angus.core.gm.interfaces.role.facade.vo.RoleDefaultVo;
import cloud.xcan.angus.core.gm.interfaces.role.facade.vo.RoleDetailVo;
import cloud.xcan.angus.core.gm.interfaces.role.facade.vo.RoleListVo;
import cloud.xcan.angus.core.gm.interfaces.role.facade.vo.RoleStatsVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserAssembler;
import cloud.xcan.angus.api.gm.user.vo.UserListVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.utils.PrincipalContextUtils;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class RoleFacadeImpl implements RoleFacade {

  @Resource
  private RoleCmd roleCmd;

  @Resource
  private RoleQuery roleQuery;

  @Resource
  private ApplicationMenuQuery applicationMenuQuery;

  @Resource
  private AuthorizationAppQuery authorizationAppQuery;

  @NameJoin
  @Override
  public RoleDetailVo create(RoleCreateDto dto) {
    Role role = RoleAssembler.toCreateDomain(dto);
    Role saved = roleCmd.create(role);
    return RoleAssembler.toDetailVo(saved);
  }

  @NameJoin
  @Override
  public RoleDetailVo update(Long id, RoleUpdateDto dto) {
    Role role = RoleAssembler.toUpdateDomain(id, dto);
    Role saved = roleCmd.update(role);
    return RoleAssembler.toDetailVo(saved);
  }

  @NameJoin
  @Override
  public RoleDetailVo updateStatus(Long id, EnabledStatusUpdateDto dto) {
    Role saved = roleCmd.updateStatus(id, dto.getStatus());
    return RoleAssembler.toDetailVo(saved);
  }

  @Override
  public void delete(Long id) {
    roleCmd.delete(id);
  }

  @NameJoin
  @Override
  public RoleDetailVo getDetail(Long id) {
    Role role = roleQuery.findAndCheck(id);
    role.setUserCount(roleQuery.countUsersByRoleId(id));
    return RoleAssembler.toDetailVo(role);
  }

  @NameJoin
  @Override
  public PageResult<RoleListVo> list(RoleFindDto dto) {
    GenericSpecification<Role> spec = RoleAssembler.getSpecification(dto);
    Page<Role> page = roleQuery.find(spec, dto.tranPage(), dto.fullTextSearch,
        getMatchSearchFields(dto.getClass()));
    if (page.hasContent()) {
      assembleUserCount(page.getContent());
    }
    return buildVoPageResult(page, RoleAssembler::toListVo);
  }

  @NameJoin
  @Override
  public List<RoleListVo> list(List<Long> ids) {
    List<Role> roles = roleQuery.findAndCheck(ids);
    if (roles.isEmpty()) {
      return new ArrayList<>();
    }

    // 批量查询所有角色的用户数
    assembleUserCount(roles);
    return roles.stream().map(RoleAssembler::toListVo).collect(Collectors.toList());
  }

  @Override
  public RoleStatsVo getStats() {
    RoleStatsVo stats = new RoleStatsVo();
    stats.setTotalRoles(roleQuery.countTotal());
    stats.setSystemRoles(roleQuery.countSystemRoles());
    stats.setCustomRoles(roleQuery.countCustomRoles());
    stats.setTotalUsers(roleQuery.countTotalUsers());
    return stats;
  }

  @Override
  public RolePermissionVo getPermissions(Long id) {
    Role role = roleQuery.findAndCheck(id);
    return RoleAssembler.toPermissionVo(role);
  }

  @Override
  public RolePermissionVo updatePermissions(Long id, RolePermissionUpdateDto dto) {
    Role role = roleCmd.updatePermissions(id, RoleAssembler.toPermissionsDomain(dto));
    return RoleAssembler.toPermissionVo(role);
  }

  @Override
  public RoleDefaultVo setDefault(Long id, RoleDefaultDto dto) {
    Role role = roleCmd.setDefault(id, dto.getIsDefault());
    return RoleAssembler.toDefaultVo(role);
  }

  @NameJoin
  @Override
  public PageResult<UserListVo> getWideUsers(Long id, RoleObjectFindDto dto) {
    Page<User> userPage = roleQuery.findWideUsersByRoleId(id, dto.getName(), dto.tranPage());
    return buildVoPageResult(userPage, UserAssembler::toListVo);
  }

  @NameJoin
  @Override
  public PageResult<UserListVo> getUsers(Long id, RoleObjectFindDto dto) {
    Page<User> userPage = roleQuery.findUsersByRoleId(id, dto.getName(), dto.tranPage());
    return buildVoPageResult(userPage, UserAssembler::toListVo);
  }

  @NameJoin
  @Override
  public PageResult<DepartmentDetailVo> getDepartments(Long id, RoleObjectFindDto dto) {
    Page<Department> departmentPage = roleQuery.findDepartmentsByRoleId(
        id, dto.getName(), dto.tranPage());
    return buildVoPageResult(departmentPage, DepartmentAssembler::toDetailVo);
  }

  @NameJoin
  @Override
  public PageResult<GroupDetailVo> getGroups(Long id, RoleObjectFindDto dto) {
    Page<Group> groupPage = roleQuery.findGroupsByRoleId(id, dto.getName(), dto.tranPage());
    return buildVoPageResult(groupPage, GroupAssembler::toDetailVo);
  }

  @Override
  public List<AuthorizableApplicationMenuVo> getAuthorizableMenus(Long roleId) {
    // 1. 获取角色信息
    Role role = roleQuery.findAndCheck(roleId);
    Long appId = role.getAppId();
    if (appId == null) {
      return new ArrayList<>();
    }

    // 2. 获取角色已授权的菜单ID列表
    Set<Long> authorizedMenuIds = new HashSet<>();
    if (isNotEmpty(role.getPermissions())) {
      for (PermissionInfo permission : role.getPermissions()) {
        if (permission.getMenuId() != null) {
          authorizedMenuIds.add(permission.getMenuId());
        }
      }
    }

    // 3. 判断当前用户是否是系统管理员
    boolean isSysAdmin = PrincipalContextUtils.isSysAdmin();
    List<ApplicationMenu> availableMenus;

    if (isSysAdmin) {
      // 4. 如果是系统管理员，获取角色对应应用的所有菜单
      availableMenus = applicationMenuQuery.findByAppId(appId).stream()
          .filter(x -> x.getStatus().isEnabled()).collect(Collectors.toList());
    } else {
      // 5. 如果不是系统管理员，获取当前用户已授权的应用菜单
      Application application = authorizationAppQuery.subjectAppList(
          AuthorizationSubjectType.USER, getUserId(), String.valueOf(appId),
          null, true, true);
      if (application == null || isEmpty(application.getMenus())) {
        return new ArrayList<>();
      }
      availableMenus = application.getMenus();
    }

    if (isEmpty(availableMenus)) {
      return new ArrayList<>();
    }

    // 6. 构建菜单树并标记已授权状态
    List<ApplicationMenuVo> menuTree = ApplicationMenuAssembler.buildMenuTree(availableMenus);
    return ApplicationMenuAssembler.convertToAuthorizableMenuTree(menuTree, authorizedMenuIds);
  }

  private void assembleUserCount(List<Role> roles) {
    List<Long> roleIds = roles.stream().map(Role::getId).collect(Collectors.toList());
    Map<Long, Long> userCountMap = roleQuery.countUsersByRoleIds(roleIds);
    // 设置每个角色的用户数
    roles.forEach(role -> {
      role.setUserCount(userCountMap.getOrDefault(role.getId(), 0L));
    });
  }

}
