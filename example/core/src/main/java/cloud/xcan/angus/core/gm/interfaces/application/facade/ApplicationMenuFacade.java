package cloud.xcan.angus.core.gm.interfaces.application.facade;

import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationMenuCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationMenuUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationMenuVo;
import java.util.List;

public interface ApplicationMenuFacade {

  /**
   * 创建应用菜单
   */
  ApplicationMenuVo createMenu(Long appId, ApplicationMenuCreateDto dto);

  /**
   * 更新应用菜单
   */
  ApplicationMenuVo updateMenu(Long appId, Long menuId, ApplicationMenuUpdateDto dto);

  /**
   * 删除应用菜单
   */
  void deleteMenu(Long appId, Long menuId);

  /**
   * 获取应用菜单树
   */
  List<ApplicationMenuVo> getMenus(Long appId);
}
