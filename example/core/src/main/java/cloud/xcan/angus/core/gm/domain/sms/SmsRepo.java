package cloud.xcan.angus.core.gm.domain.sms;

import cloud.xcan.angus.api.commonlink.sms.SmsStatus;
import cloud.xcan.angus.core.gm.domain.sms.enums.SmsType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface SmsRepo extends BaseRepository<Sms, Long> {

  /**
   * 根据状态查询SMS列表
   */
  List<Sms> findByStatus(SmsStatus status);

  /**
   * 根据类型查询SMS列表
   */
  List<Sms> findByType(SmsType type);

  /**
   * 根据手机号查询SMS列表
   */
  List<Sms> findByPhone(String phone);

  /**
   * 根据外部ID查询
   */
  Optional<Sms> findByExternalId(String externalId);

  /**
   * 根据状态和发送时间范围查询
   */
  List<Sms> findByStatusAndSendTimeBetween(SmsStatus status, LocalDateTime start,
      LocalDateTime end);

  /**
   * 统计指定状态的SMS数量
   */
  long countByStatus(SmsStatus status);

  /**
   * 统计指定时间范围内的短信数量
   */
  @Query("SELECT COUNT(s) FROM Sms s WHERE s.sendTime >= :startTime AND s.sendTime <= :endTime")
  long countBySendTimeBetween(@Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);

  /**
   * 统计指定类型的短信数量
   */
  long countByType(SmsType type);

  /**
   * 根据模板ID查询短信列表
   */
  List<Sms> findByTemplateId(Long templateId);

  /**
   * 统计指定模板ID的短信数量
   */
  long countByTemplateId(Long templateId);

  /**
   * 根据规范查询所有短信
   */
  Page<Sms> findAll(Specification<Sms> spec, Pageable pageable);

  /**
   * 统计指定租户ID列表的短信数量
   */
  @Query(value = "SELECT COUNT(*) FROM gm_sms s WHERE s.tenant_id IN :tenantIds", nativeQuery = true)
  long countByTenantIdIn(@Param("tenantIds") List<Long> tenantIds);
}
