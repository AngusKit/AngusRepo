package cloud.xcan.angus.api.commonlink.application;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

/**
 * 应用菜单仓储接口
 */
@Repository("commonApplicationMenuRepo")
public interface ApplicationMenuRepo extends NameJoinRepository<ApplicationMenu, Long>,
    BaseRepository<ApplicationMenu, Long> {

  /**
   * 根据应用ID查找菜单列表
   */
  List<ApplicationMenu> findByApplicationId(Long appId);

  /**
   * 根据应用ID和菜单ID集合查找菜单列表
   */
  List<ApplicationMenu> findByApplicationIdAndIdIn(Long appId, Collection<Long> menuIds);

  /**
   * 根据应用ID和状态查找菜单列表
   */
  List<ApplicationMenu> findByApplicationIdAndStatus(Long id, EnabledStatus status);

  /**
   * 根据应用ID列表查找菜单列表
   */
  List<ApplicationMenu> findByApplicationIdInAndStatus(List<Long> appIds, EnabledStatus status);

  /**
   * 根据应用ID和父菜单ID查找菜单列表
   */
  List<ApplicationMenu> findByApplicationIdAndParentId(Long appId, Long parentId);

  /**
   * 检查应用下是否存在指定编码的菜单
   */
  boolean existsByApplicationIdAndCode(Long appId, String code);

  /**
   * 统计指定应用的菜单数量
   */
  Integer countByApplicationId(Long id);

  /**
   * 检查应用下是否存在指定编码的菜单（排除指定ID）
   */
  boolean existsByApplicationIdAndCodeAndIdNot(Long appId, String code, Long id);

  /**
   * 根据应用ID删除所有菜单
   */
  @Modifying
  void deleteByApplicationId(Long id);

  /**
   * 根据租户ID删除所有应用菜单
   */
  @Modifying
  void deleteByTenantId(Long tenantId);

}

