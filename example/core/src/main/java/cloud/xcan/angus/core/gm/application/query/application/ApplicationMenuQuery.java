package cloud.xcan.angus.core.gm.application.query.application;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.application.ApplicationMenu;
import java.util.Collection;
import java.util.List;

public interface ApplicationMenuQuery {

  /**
   * 根据ID查找菜单并检查是否存在
   */
  ApplicationMenu findAndCheck(Long id);

  /**
   * 根据应用ID和菜单ID集合查找菜单列表并检查是否全部存在
   */
  List<ApplicationMenu> findAndCheck(Long appId, Collection<Long> menuIds);

  /**
   * 根据应用ID查找菜单列表
   */
  List<ApplicationMenu> findByAppId(Long appId);

  /**
   * 统计指定应用的菜单数量
   */
  Integer countByApplicationId(Long id);

  /**
   * 检查应用下是否存在指定编码的菜单
   */
  boolean existsByAppIdAndCode(Long appId, String code);

  /**
   * 检查应用下是否存在指定编码的菜单（排除指定ID）
   */
  boolean existsByAppIdAndCodeAndIdNot(Long appId, String code, Long id);

}

