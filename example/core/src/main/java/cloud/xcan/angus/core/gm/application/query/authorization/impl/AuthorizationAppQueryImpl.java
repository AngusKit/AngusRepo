package cloud.xcan.angus.core.gm.application.query.authorization.impl;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.api.commonlink.application.ApplicationMenu;
import cloud.xcan.angus.api.commonlink.application.ApplicationMenuRepo;
import cloud.xcan.angus.api.commonlink.application.ApplicationRepo;
import cloud.xcan.angus.api.commonlink.role.PermissionInfo;
import cloud.xcan.angus.api.commonlink.role.Role;
import cloud.xcan.angus.api.commonlink.role.RoleRepo;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.application.query.authorization.AuthorizationAppQuery;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationRepo;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationRoleRepo;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.core.utils.PrincipalContextUtils;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

/**
 * 授权应用查询服务实现，根据授权主体（用户/部门/组）查询应用和菜单
 */
@Service
public class AuthorizationAppQueryImpl implements AuthorizationAppQuery {

  @Resource
  private AuthorizationRepo authorizationRepo;

  @Resource
  private AuthorizationRoleRepo authorizationRoleRepo;

  @Resource
  private ApplicationRepo applicationRepo;

  @Resource
  private ApplicationQuery applicationQuery;

  @Resource
  private ApplicationMenuRepo applicationMenuRepo;

  @Resource
  private UserManager userManager;

  @Resource
  private RoleRepo roleRepo;

  @Resource
  private ApplicationInfo applicationInfo;

  @Override
  public List<Application> subjectAppList(AuthorizationSubjectType subjectType, Long subjectId,
      boolean joinMenu, boolean onlyEnabled) {
    return new BizTemplate<List<Application>>(false) {
      @Override
      protected List<Application> process() {
        // 如果是系统管理员，返回所有应用
        boolean isSysAdmin = PrincipalContextUtils.isSysAdmin();
        if (isSysAdmin) {
          // 查询所有应用
          List<Application> applications = applicationQuery.findAll();

          // 如果只返回启用的应用，进行过滤
          if (onlyEnabled) {
            applications = applications.stream()
                .filter(app -> app.getStatus() == EnabledStatus.ENABLED)
                .collect(Collectors.toList());
          }

          // 如果 joinMenu=true，为每个应用查询并设置所有启用的菜单
          if (joinMenu && !isEmpty(applications)) {
            List<Long> appIds = applications.stream()
                .map(Application::getId)
                .collect(Collectors.toList());

            // 批量查询所有菜单
            List<ApplicationMenu> allMenus = applicationMenuRepo.findByApplicationIdInAndStatus(
                appIds, EnabledStatus.ENABLED);

            // 按应用ID分组
            Map<Long, List<ApplicationMenu>> menusByAppId = allMenus.stream()
                .collect(Collectors.groupingBy(ApplicationMenu::getApplicationId));

            // 为每个应用设置所有启用的菜单
            for (Application app : applications) {
              List<ApplicationMenu> appMenus = menusByAppId.getOrDefault(app.getId(),
                  new ArrayList<>());
              app.setMenus(appMenus);
            }
          }

          return applications;
        }

        // 查询该主体的授权
        Set<Long> subjectIds = new HashSet<>();
        subjectIds.add(subjectId);
        // 如果是授权主体是用户，查询用户所属组和部门授权
        if (subjectType.isUser()) {
          subjectIds.addAll(userManager.findValidOrgIdsById(subjectId));
        }

        // 提取授权ID列表
        List<Long> authorizationIds = authorizationRepo.findAuthorizationIdsBySubjectIdInAndStatus(
            subjectIds, EnabledStatus.ENABLED);
        if (isEmpty(authorizationIds)) {
          return new ArrayList<>();
        }

        // 通过授权角色关系表批量查找所有关联的角色ID
        List<Long> roleIds = authorizationRoleRepo.findRoleIdsByAuthorizationIdIn(authorizationIds);
        if (isEmpty(roleIds)) {
          return new ArrayList<>();
        }

        // 查询角色，获取应用编码列表
        List<Role> roles = roleRepo.findByIdInAndStatus(roleIds, EnabledStatus.ENABLED);
        if (isEmpty(roles)) {
          return new ArrayList<>();
        }

        List<Long> appIds = roles.stream().map(Role::getAppId).distinct()
            .collect(Collectors.toList());

        if (isEmpty(appIds)) {
          return new ArrayList<>();
        }

        // 批量查询应用（通过编码列表）
        List<Application> applications = new ArrayList<>();
        if (!isEmpty(appIds)) {
          applications = applicationRepo.findAllById(appIds);
        }

        // 如果只返回启用的应用，进行过滤
        if (onlyEnabled) {
          applications = applications.stream()
              .filter(app -> app.getStatus() == EnabledStatus.ENABLED)
              .collect(Collectors.toList());
        }

        // 如果 joinMenu=true，批量关联查询菜单并过滤
        if (joinMenu && !isEmpty(applications)) {
          // 收集所有角色的权限信息，提取允许的菜单ID
          Set<Long> allowedMenuIds = new HashSet<>();
          for (Role role : roles) {
            if (role.getPermissions() != null) {
              for (PermissionInfo permission : role.getPermissions()) {
                if (permission.getMenuId() != null) {
                  allowedMenuIds.add(permission.getMenuId());
                }
              }
            }
          }

          // 收集所有应用ID
          appIds = applications.stream()
              .map(Application::getId)
              .collect(Collectors.toList());

          // 批量查询所有菜单
          List<ApplicationMenu> allMenus = applicationMenuRepo.findByApplicationIdInAndStatus(
              appIds, EnabledStatus.ENABLED);

          // 按应用ID分组
          Map<Long, List<ApplicationMenu>> menusByAppId = allMenus.stream()
              .collect(Collectors.groupingBy(ApplicationMenu::getApplicationId));

          // 为每个应用设置过滤后的菜单
          for (Application app : applications) {
            List<ApplicationMenu> appMenus = menusByAppId.getOrDefault(app.getId(),
                new ArrayList<>());
            // 过滤菜单：只保留允许的菜单ID且状态为启用
            List<ApplicationMenu> filteredMenus
                = filterMenusByPermissions(appMenus, allowedMenuIds);
            app.setMenus(filteredMenus);
          }
        }
        return applications;
      }
    }.execute();
  }

  @Override
  public Application subjectAppList(AuthorizationSubjectType subjectType, Long subjectId,
      String appIdOrCode, String editionType, boolean joinMenu, boolean onlyEnabled) {
    return new BizTemplate<Application>(false) {
      @Override
      protected Application process() {
        // 先查询应用（通过ID或编码）
        Application application = NumberUtils.isDigits(appIdOrCode) ?
            applicationRepo.findById(Long.parseLong(appIdOrCode)).orElse(null) :
            applicationQuery.findByCodeAndEditionType(appIdOrCode,
                nullSafe(editionType, applicationInfo.getEditionType())).orElse(null);

        if (application == null) {
          return null;
        }

        // 如果 onlyEnabled=true，检查应用是否启用
        if (onlyEnabled && application.getStatus() != EnabledStatus.ENABLED) {
          return null;
        }

        // 如果不关联菜单时直接返回应用
        if (!joinMenu) {
          return application;
        }

        boolean isSysAdmin = PrincipalContextUtils.isSysAdmin();
        if (isSysAdmin) {
          // 如果是系统管理员，返回所有菜单
          List<ApplicationMenu> allMenus = applicationMenuRepo.findByApplicationIdAndStatus(
              application.getId(), EnabledStatus.ENABLED);
          application.setMenus(allMenus);
          return application;
        } else {
          // 查询该主体的授权
          Set<Long> subjectIds = new HashSet<>();
          subjectIds.add(subjectId);
          // 如果是授权主体是用户，查询用户所属组和部门授权
          if (subjectType.isUser()) {
            subjectIds.addAll(userManager.findValidOrgIdsById(subjectId));
          }

          // 提取授权ID列表
          List<Long> authorizationIds = authorizationRepo.findAuthorizationIdsBySubjectIdInAndStatus(
              subjectIds, EnabledStatus.ENABLED);
          if (isEmpty(authorizationIds)) {
            return null;
          }

          if (isNotEmpty(authorizationIds)) {
            // 通过授权角色关系表批量查找所有关联的角色ID
            List<Long> roleIds = authorizationRoleRepo.findRoleIdsByAuthorizationIdIn(
                authorizationIds);
            if (isEmpty(roleIds)) {
              return null;
            }

            // 查询角色，获取应用编码列表
            List<Role> roles = roleRepo.findByAppIdAndIdInAndStatus(application.getId(),
                roleIds, EnabledStatus.ENABLED);
            if (isEmpty(roles)) {
              return null;
            }

            List<ApplicationMenu> allMenus = applicationMenuRepo.findByApplicationIdAndStatus(
                application.getId(), EnabledStatus.ENABLED);

            // 收集所有角色的权限信息，提取允许的菜单ID
            Set<Long> allowedMenuIds = new HashSet<>();
            for (Role role : roles) {
              if (role.getPermissions() != null) {
                for (PermissionInfo permission : role.getPermissions()) {
                  if (permission.getMenuId() != null) {
                    allowedMenuIds.add(permission.getMenuId());
                  }
                }
              }
            }

            // 过滤菜单：只保留允许的菜单ID且状态为启用
            List<ApplicationMenu> filteredMenus = filterMenusByPermissions(allMenus,
                allowedMenuIds);
            application.setMenus(filteredMenus);
          }
        }
        return application;
      }
    }.execute();
  }

  /**
   * 根据角色权限过滤菜单 只保留允许的菜单ID且状态为启用的菜单，如果父菜单被过滤掉，子菜单也会被过滤掉
   */
  private List<ApplicationMenu> filterMenusByPermissions(
      List<ApplicationMenu> allMenus, Set<Long> allowedMenuIds) {
    if (isEmpty(allMenus)) {
      return new ArrayList<>();
    }

    if (isEmpty(allowedMenuIds)) {
      // 如果没有允许的菜单ID，返回空列表
      return new ArrayList<>();
    }

    // 构建菜单ID到菜单的映射
    Map<Long, ApplicationMenu> menuMap = allMenus.stream()
        .collect(Collectors.toMap(ApplicationMenu::getId, menu -> menu));

    // 第一步：过滤出允许的菜单ID且状态为启用的菜单
    Set<Long> validMenuIds = new HashSet<>();
    for (ApplicationMenu menu : allMenus) {
      if ((!menu.getRequiresAuth() || allowedMenuIds.contains(menu.getId()))
          && menu.getStatus() == EnabledStatus.ENABLED) {
        validMenuIds.add(menu.getId());
      }
    }

    // 第二步：检查每个菜单的所有父菜单是否都在允许列表中
    // 如果父菜单链中任何一个不在允许列表中，则移除该菜单及其所有子菜单
    Set<Long> finalMenuIds = new HashSet<>();
    for (Long menuId : validMenuIds) {
      if (isMenuPathValid(menuId, menuMap, allowedMenuIds)) {
        finalMenuIds.add(menuId);
      }
    }

    // 第三步：返回最终过滤后的菜单列表
    return allMenus.stream()
        .filter(menu -> finalMenuIds.contains(menu.getId()))
        .collect(Collectors.toList());
  }

  /**
   * 检查菜单及其所有父菜单是否都在允许列表中 从当前菜单开始，递归向上检查父菜单链
   */
  private boolean isMenuPathValid(Long menuId, Map<Long, ApplicationMenu> menuMap,
      Set<Long> allowedMenuIds) {
    ApplicationMenu menu = menuMap.get(menuId);
    if (menu == null) {
      return false;
    }

    // 检查当前菜单是否在允许列表中
    if (menu.getRequiresAuth() && !allowedMenuIds.contains(menuId)) {
      return false;
    }

    // 如果是根菜单（没有父菜单），直接返回true
    if (menu.getParentId() == null) {
      return true;
    }

    // 递归检查父菜单是否在允许列表中
    return isMenuPathValid(menu.getParentId(), menuMap, allowedMenuIds);
  }
}
