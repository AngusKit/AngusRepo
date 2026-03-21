package cloud.xcan.angus.core.gm.domain.authorization;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * <p>
 * 授权角色关系仓储接口
 * </p>
 */
@NoRepositoryBean
public interface AuthorizationRoleRepo extends BaseRepository<AuthorizationRole, Long> {

  /**
   * 根据授权ID查找所有角色关系
   */
  List<AuthorizationRole> findByAuthorizationId(Long authorizationId);

  /**
   * 根据授权ID列表查找所有角色关系
   */
  List<AuthorizationRole> findByAuthorizationIdIn(Collection<Long> authorizationIds);

  /**
   * 根据角色ID查找所有授权关系
   */
  List<AuthorizationRole> findByRoleId(Long roleId);

  /**
   * 根据角色ID列表查找所有授权关系
   */
  List<AuthorizationRole> findByRoleIdIn(Collection<Long> roleIds);

  /**
   * 根据授权ID列表查找对应的角色ID集合
   */
  @Query(value = "SELECT DISTINCT ar.roleId FROM AuthorizationRole ar WHERE ar.authorizationId IN ?1")
  List<Long> findRoleIdsByAuthorizationIdIn(Collection<Long> authorizationIds);

  /**
   * 统计指定角色ID的授权关系数量
   */
  Long countByRoleId(Long roleId);

  /**
   * 检查授权ID和角色ID的关系是否存在
   */
  boolean existsByAuthorizationIdAndRoleId(Long authorizationId, Long roleId);

  /**
   * 根据授权ID删除所有角色关系
   */
  @Modifying
  void deleteByAuthorizationId(Long authorizationId);

  /**
   * 根据授权ID和角色ID删除关系
   */
  @Modifying
  void deleteByAuthorizationIdAndRoleId(Long authorizationId, Long roleId);

  /**
   * 根据授权ID列表删除所有关系
   */
  @Modifying
  void deleteAllByAuthorizationIdIn(Collection<Long> authorizationIds);

  /**
   * 根据角色ID删除所有授权关系
   */
  @Modifying
  void deleteByRoleId(Long roleId);

  /**
   * 根据角色ID列表删除所有授权关系
   */
  @Modifying
  void deleteByRoleIdIn(List<Long> roleIds);

  /**
   * 根据租户ID删除所有授权角色关系
   */
  @Modifying
  void deleteByTenantId(Long tenantId);

}
