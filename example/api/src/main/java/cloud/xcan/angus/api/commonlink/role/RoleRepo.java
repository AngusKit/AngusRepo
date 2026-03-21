package cloud.xcan.angus.api.commonlink.role;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 角色仓储接口
 */
@Repository("commonRoleRepo")
public interface RoleRepo extends NameJoinRepository<Role, Long>, BaseRepository<Role, Long> {

  /**
   * 检查名称是否存在
   */
  boolean existsByName(String name);

  /**
   * 检查名称是否存在（排除指定ID）
   */
  boolean existsByNameAndIdNot(String name, Long id);

  /**
   * 检查编码是否存在
   */
  boolean existsByCode(String code);

  /**
   * 统计指定状态的角色数量
   */
  long countByStatus(EnabledStatus status);

  /**
   * 统计系统角色数量
   */
  long countByIsSystemTrue();

  /**
   * 统计自定义角色数量
   */
  long countByIsSystemFalse();

  /**
   * 统计指定应用的角色数量
   */
  long countByAppId(Long appId);

  /**
   * 根据应用ID查找角色列表
   */
  List<Role> findByAppId(Long appId);

  /**
   * 根据应用ID查找默认角色
   */
  Role findByAppIdAndIsDefaultTrue(Long appId);

  /**
   * 查询租户下所有启用的默认角色（isDefault=true 针对所有用户自动生效）
   */
  List<Role> findByIsDefaultTrueAndStatus(EnabledStatus status);

  /**
   * 根据应用ID、角色ID列表和状态查找角色列表
   */
  List<Role> findByAppIdAndIdInAndStatus(Long appId, List<Long> roleIds, EnabledStatus status);

  /**
   * 根据角色ID列表和状态查找角色列表
   */
  List<Role> findByIdInAndStatus(List<Long> roleIds, EnabledStatus status);

  /**
   * 根据应用ID查找角色列表（包含自定义和系统角色）
   */
  @Query(value = "SELECT DISTINCT * FROM gm_role WHERE app_id = ?1 AND (tenant_id = ?2 OR is_system = 1)", nativeQuery = true)
  List<Role> findWideRolesByAppIdAndTenantId(Long appId, Long tenantId);

  @Query(value = "SELECT DISTINCT * FROM gm_role WHERE tenant_id = ?1 OR is_system = 1", nativeQuery = true)
  List<Role> findWideRolesByTenantId(Long tenantId);

  @Modifying
  void deleteByAppId(Long appId);

  /**
   * 根据租户ID删除所有角色（排除系统角色和默认角色）
   */
  @Modifying
  void deleteByTenantId(Long tenantId);


}
