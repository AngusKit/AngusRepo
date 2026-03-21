package cloud.xcan.angus.api.commonlink.tenant;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.tenant.enums.AccountType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("commonTenantRepo")
public interface TenantRepo extends NameJoinRepository<Tenant, Long>, BaseRepository<Tenant, Long> {

  /**
   * 通过主租户ID查找子租户列表
   */
  List<Tenant> findByMainTenantId(Long id);

  /**
   * 查找所有主账号租户（用于许可配额同步等全局任务）
   */
  List<Tenant> findByAccountType(AccountType accountType);

  /**
   * 根据租户ID查询租户
   */
  @Query(value = "SELECT * FROM gm_tenant WHERE id IN ?1", nativeQuery = true)
  List<Tenant> findAllByIdIn(Collection<Long> ids);

  /**
   * 查询第一个租户
   */
  @Query(value = "SELECT * FROM gm_tenant limit 1", nativeQuery = true)
  Optional<Tenant> findFirst();

  /**
   * 通过租户编码检查租户是否存在
   */
  boolean existsByCode(String code);

  /**
   * 通过租户编码和排除指定ID检查租户是否存在
   */
  boolean existsByCodeAndIdNot(String code, Long id);

  /**
   * 统计指定状态的租户数量
   */
  long countByStatus(EnabledStatus status);

  /**
   * 统计在指定日期之后创建的租户数量
   */
  long countByCreatedDateAfter(LocalDateTime date);

}
