package cloud.xcan.angus.core.gm.domain.email;

import cloud.xcan.angus.api.commonlink.email.EmailStatus;
import cloud.xcan.angus.core.gm.domain.email.enums.EmailType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 邮件仓储接口 提供邮件的查询和统计功能
 */
public interface EmailRepo extends BaseRepository<Email, Long> {

  /**
   * 根据状态查询邮件列表
   */
  List<Email> findByStatus(EmailStatus status);

  /**
   * 根据类型查询邮件列表
   */
  List<Email> findByType(EmailType type);

  /**
   * 根据状态统计邮件数量
   */
  long countByStatus(EmailStatus status);

  /**
   * 根据模板ID统计邮件数量
   */
  @Query("SELECT COUNT(e) FROM Email e WHERE e.templateId = :templateId")
  long countByTemplateId(@Param("templateId") Long templateId);

  /**
   * 根据模板ID列表和状态批量查询邮件（用于统计）
   */
  @Query("SELECT e FROM Email e WHERE e.templateId IN :templateIds AND e.status = :status")
  List<Email> findByTemplateIdInAndStatus(@Param("templateIds") List<Long> templateIds,
      @Param("status") EmailStatus status);

  /**
   * 统计发送时间在指定时间范围且状态为已发送的邮件数量（用于统计，性能更好）
   */
  @Query("SELECT COUNT(e) FROM Email e WHERE e.status = :status AND e.sendTime >= :startTime AND e.sendTime <= :endTime")
  long countByStatusAndSendTimeBetween(@Param("status") EmailStatus status,
      @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

}
