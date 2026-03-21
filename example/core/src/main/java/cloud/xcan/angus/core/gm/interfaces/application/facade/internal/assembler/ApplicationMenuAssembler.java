package cloud.xcan.angus.core.gm.interfaces.application.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.application.ApplicationMenu;
import cloud.xcan.angus.api.commonlink.role.PermissionInfo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationMenuCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationMenuUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.PermissionDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationMenuVo;
import cloud.xcan.angus.core.gm.interfaces.role.facade.vo.AuthorizableApplicationMenuVo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationMenuAssembler {

  public static ApplicationMenu toDomain(Long appId, ApplicationMenuCreateDto dto) {
    ApplicationMenu menu = new ApplicationMenu();
    menu.setApplicationId(appId);
    menu.setName(dto.getName());
    menu.setShowName(nullSafe(dto.getShowName(), dto.getName()));
    menu.setCode(dto.getCode());
    menu.setIcon(dto.getIcon());
    menu.setPath(dto.getPath());
    menu.setParentId(dto.getParentId());
    menu.setSortOrder(nullSafe(dto.getSortOrder(), 1));
    menu.setStatus(nullSafe(dto.getStatus(), EnabledStatus.ENABLED));
    menu.setType(dto.getType());
    menu.setRequiresAuth(nullSafe(dto.getRequiresAuth(), false));
    menu.setPermission(toPermissionInfo(dto.getPermission()));
    return menu;
  }

  public static ApplicationMenu toDomain(Long appId, Long menuId, ApplicationMenuUpdateDto dto) {
    ApplicationMenu menu = new ApplicationMenu();
    menu.setId(menuId);
    menu.setApplicationId(appId);
    menu.setName(dto.getName());
    menu.setShowName(nullSafe(dto.getShowName(), dto.getName()));
    menu.setCode(dto.getCode());
    menu.setIcon(dto.getIcon());
    menu.setPath(dto.getPath());
    menu.setParentId(dto.getParentId());
    menu.setSortOrder(nullSafe(dto.getSortOrder(), 1));
    menu.setStatus(nullSafe(dto.getStatus(), EnabledStatus.ENABLED));
    menu.setType(dto.getType());
    menu.setRequiresAuth(nullSafe(dto.getRequiresAuth(), false));
    menu.setPermission(toPermissionInfo(dto.getPermission()));
    return menu;
  }

  public static PermissionInfo toPermissionInfo(PermissionDto dto) {
    return isNull(dto) ? new PermissionInfo() : new PermissionInfo()
        .setResource(dto.getResource())
        .setResourceName(dto.getResourceName())
        .setActions(dto.getActions());
  }

  public static ApplicationMenuVo toVo(ApplicationMenu menu) {
    ApplicationMenuVo vo = new ApplicationMenuVo();
    vo.setId(menu.getId());
    vo.setApplicationId(menu.getApplicationId());
    vo.setName(menu.getName());
    vo.setShowName(menu.getShowName());
    vo.setCode(menu.getCode());
    vo.setIcon(menu.getIcon());
    vo.setPath(menu.getPath());
    vo.setParentId(menu.getParentId());
    vo.setSortOrder(menu.getSortOrder());
    vo.setStatus(menu.getStatus());
    vo.setType(menu.getType());
    vo.setRequiresAuth(menu.getRequiresAuth());

    // 设置权限信息
    if (menu.getPermission() != null) {
      PermissionInfo permissionInfo = menu.getPermission();
      permissionInfo.setParentMenuId(menu.getParentId());
      permissionInfo.setMenuId(menu.getId());
      permissionInfo.setMenuName(menu.getName());
      vo.setPermission(permissionInfo);
    } else {
      vo.setPermission(new PermissionInfo());
    }

    // 设置审计字段
    vo.setTenantId(menu.getTenantId());
    vo.setCreatedBy(menu.getCreatedBy());
    vo.setCreatedDate(menu.getCreatedDate());
    vo.setModifiedBy(menu.getModifiedBy());
    vo.setModifiedDate(menu.getModifiedDate());
    return vo;
  }

  /**
   * 从扁平列表构建菜单树
   */
  public static List<ApplicationMenuVo> buildMenuTree(List<ApplicationMenu> menus) {
    if (menus == null || menus.isEmpty()) {
      return new ArrayList<>();
    }

    // Convert to VO list
    List<ApplicationMenuVo> voList = menus.stream()
        .map(ApplicationMenuAssembler::toVo)
        .toList();

    // Build tree structure
    Map<Long, ApplicationMenuVo> voMap = voList.stream()
        .collect(Collectors.toMap(ApplicationMenuVo::getId, vo -> vo));

    List<ApplicationMenuVo> rootMenus = new ArrayList<>();
    for (ApplicationMenuVo vo : voList) {
      if (vo.getParentId() == null || vo.getParentId() <= 0) {
        rootMenus.add(vo);
      } else {
        ApplicationMenuVo parent = voMap.get(vo.getParentId());
        if (parent != null) {
          if (parent.getChildren() == null) {
            parent.setChildren(new ArrayList<>());
          }
          parent.getChildren().add(vo);
        }
      }
    }

    // Sort menus by sortOrder
    rootMenus.sort((a, b) -> {
      int orderA = a.getSortOrder() != null ? a.getSortOrder() : 0;
      int orderB = b.getSortOrder() != null ? b.getSortOrder() : 0;
      return Integer.compare(orderA, orderB);
    });

    // Sort children recursively
    sortMenuChildren(rootMenus);
    return rootMenus;
  }

  /**
   * 递归排序菜单子节点
   */
  private static void sortMenuChildren(List<ApplicationMenuVo> menus) {
    if (menus == null || menus.isEmpty()) {
      return;
    }
    menus.sort((a, b) -> {
      int orderA = a.getSortOrder() != null ? a.getSortOrder() : 0;
      int orderB = b.getSortOrder() != null ? b.getSortOrder() : 0;
      return Integer.compare(orderA, orderB);
    });
    for (ApplicationMenuVo menu : menus) {
      if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
        sortMenuChildren(menu.getChildren());
      }
    }
  }


  /**
   * 将 ApplicationMenuVo 树转换为 AuthorizableApplicationMenuVo 树，并标记已授权状态
   */
  public static List<AuthorizableApplicationMenuVo> convertToAuthorizableMenuTree(
      List<ApplicationMenuVo> menuTree, Set<Long> authorizedMenuIds) {
    if (isEmpty(menuTree)) {
      return new ArrayList<>();
    }

    List<AuthorizableApplicationMenuVo> result = new ArrayList<>();
    for (ApplicationMenuVo menu : menuTree) {
      AuthorizableApplicationMenuVo authorizableMenu = convertToAuthorizableMenu(menu,
          authorizedMenuIds);
      result.add(authorizableMenu);
    }
    return result;
  }

  /**
   * 将单个 ApplicationMenuVo 转换为 AuthorizableApplicationMenuVo，并标记已授权状态
   */
  public static AuthorizableApplicationMenuVo convertToAuthorizableMenu(
      ApplicationMenuVo menu, Set<Long> authorizedMenuIds) {
    AuthorizableApplicationMenuVo authorizableMenu = new AuthorizableApplicationMenuVo();

    // 复制基本属性
    authorizableMenu.setId(menu.getId());
    authorizableMenu.setApplicationId(menu.getApplicationId());
    authorizableMenu.setName(menu.getName());
    authorizableMenu.setShowName(menu.getShowName());
    authorizableMenu.setCode(menu.getCode());
    authorizableMenu.setIcon(menu.getIcon());
    authorizableMenu.setPath(menu.getPath());
    authorizableMenu.setParentId(menu.getParentId());
    authorizableMenu.setSortOrder(menu.getSortOrder());
    authorizableMenu.setStatus(menu.getStatus());
    authorizableMenu.setType(menu.getType());
    authorizableMenu.setRequiresAuth(menu.getRequiresAuth());
    authorizableMenu.setPermission(menu.getPermission());
    // 标记是否已授权
    boolean authorized = menu.getId() != null && authorizedMenuIds.contains(menu.getId());
    authorizableMenu.setAuthorized(authorized);

    // 递归处理子菜单
    if (isNotEmpty(menu.getChildren())) {
      List<AuthorizableApplicationMenuVo> children = new ArrayList<>();
      for (ApplicationMenuVo child : menu.getChildren()) {
        children.add(convertToAuthorizableMenu(child, authorizedMenuIds));
      }
      authorizableMenu.setChildren(children);
    }

    return authorizableMenu;
  }

}

