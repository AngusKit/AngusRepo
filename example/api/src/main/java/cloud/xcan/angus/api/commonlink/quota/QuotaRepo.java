package cloud.xcan.angus.api.commonlink.quota;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 资源配额仓储接口
 */
@Repository("commonQuotaRepo")
public interface QuotaRepo extends BaseRepository<Quota, Long> {

  /**
   * 根据租户ID查询所有配额
   */
  List<Quota> findByTenantId(Long tenantId);

  /**
   * 根据租户ID和编码查询
   */
  Optional<Quota> findByTenantIdAndCode(Long tenantId, String code);

  /**
   * 根据租户ID、编码和是否模板查询配额
   */
  @Query("SELECT q FROM Quota q WHERE q.code = :code AND q.isInitTemplate = :isInitTemplate")
  Optional<Quota> findByTenantIdAndCodeAndIsInitTemplate(String code, Boolean isInitTemplate);

  /**
   * 查询所有模板配额（isInitTemplate=true）
   */
  @Query("SELECT q FROM Quota q WHERE q.isInitTemplate = true")
  List<Quota> findAllTemplateQuotas();

  /**
   * 根据租户ID删除所有配额
   */
  @Modifying
  void deleteByTenantId(Long tenantId);


}
