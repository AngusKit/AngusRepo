package cloud.xcan.angus.core.gm.application.cmd.application;

import cloud.xcan.angus.api.commonlink.application.ApplicationMenu;

public interface ApplicationMenuCmd {

  /**
   * 创建菜单
   */
  ApplicationMenu create(ApplicationMenu menu);

  /**
   * 更新菜单
   */
  ApplicationMenu update(ApplicationMenu menu);

  /**
   * 删除菜单
   */
  void delete(Long appId, Long menuId);

  /**
   * 删除应用菜单
   */
  void deleteByApplicationId(Long id);
}

