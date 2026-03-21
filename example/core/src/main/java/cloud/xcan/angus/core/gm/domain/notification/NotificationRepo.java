package cloud.xcan.angus.core.gm.domain.notification;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * 通知仓储接口
 */
@NoRepositoryBean
public interface NotificationRepo extends BaseRepository<Notification, Long> {

  /**
   * 统计未读通知数量
   */
  @Query("SELECT COUNT(n) FROM Notification n WHERE n.targetUserId = :targetUserId OR n.targetUserId IS NULL")
  long countAll(@Param("targetUserId") Long targetUserId);

  /**
   * 统计未读通知数量
   */
  @Query("SELECT COUNT(n) FROM Notification n WHERE (n.targetUserId = :targetUserId OR n.targetUserId IS NULL) AND n.isRead = false AND n.isArchived = false")
  long countUnread(@Param("targetUserId") Long targetUserId);

  /**
   * 统计星标通知数量
   */
  @Query("SELECT COUNT(n) FROM Notification n WHERE (n.targetUserId = :targetUserId OR n.targetUserId IS NULL) AND n.isStarred = true AND n.isArchived = false")
  long countStarred(@Param("targetUserId") Long targetUserId);

  /**
   * 统计归档通知数量
   */
  @Query("SELECT COUNT(n) FROM Notification n WHERE (n.targetUserId = :targetUserId OR n.targetUserId IS NULL) AND n.isArchived = true")
  long countArchived(@Param("targetUserId") Long targetUserId);

  /**
   * 统计今日新增通知数量
   */
  @Query("SELECT COUNT(n) FROM Notification n WHERE (n.targetUserId = :targetUserId OR n.targetUserId IS NULL) AND DATE(n.timestamp) = CURRENT_DATE")
  long countTodayNew(@Param("targetUserId") Long targetUserId);

  /**
   * 按类型统计通知数量
   */
  @Query("SELECT n.type, COUNT(n) FROM Notification n WHERE (n.targetUserId = :targetUserId OR n.targetUserId IS NULL) GROUP BY n.type")
  List<Object[]> countByType(@Param("targetUserId") Long targetUserId);

  /**
   * 按优先级统计通知数量
   */
  @Query("SELECT n.priority, COUNT(n) FROM Notification n WHERE (n.targetUserId = :targetUserId OR n.targetUserId IS NULL) GROUP BY n.priority")
  List<Object[]> countByPriority(@Param("targetUserId") Long targetUserId);

  /**
   * 按分类统计通知数量
   */
  @Query("SELECT n.category, COUNT(n) FROM Notification n WHERE (n.targetUserId = :targetUserId OR n.targetUserId IS NULL) GROUP BY n.category")
  List<Object[]> countByCategory(@Param("targetUserId") Long targetUserId);

  /**
   * 批量更新已读状态
   */
  @Modifying
  @Transactional
  @Query("UPDATE Notification n SET n.isRead = :isRead WHERE n.id IN :ids")
  void updateReadStatus(@Param("ids") List<Long> ids, @Param("isRead") Boolean isRead);

  /**
   * 批量更新星标状态
   */
  @Modifying
  @Transactional
  @Query("UPDATE Notification n SET n.isStarred = :isStarred WHERE n.id IN :ids")
  void updateStarredStatus(@Param("ids") List<Long> ids, @Param("isStarred") Boolean isStarred);

  /**
   * 批量归档
   */
  @Modifying
  @Transactional
  @Query("UPDATE Notification n SET n.isArchived = true WHERE n.id IN :ids")
  void archiveByIds(@Param("ids") List<Long> ids);

  /**
   * 标记所有未读为已读
   */
  @Modifying
  @Transactional
  @Query("UPDATE Notification n SET n.isRead = true WHERE (n.targetUserId = :targetUserId OR n.targetUserId IS NULL) AND n.isRead = false AND n.isArchived = false")
  int markAllAsRead(@Param("targetUserId") Long targetUserId);

  /**
   * 根据时间范围查询
   */
  @Query("SELECT n FROM Notification n WHERE (n.targetUserId = :targetUserId OR n.targetUserId IS NULL) AND n.timestamp BETWEEN :startTime AND :endTime ORDER BY n.timestamp DESC")
  List<Notification> findByTimeRange(@Param("targetUserId") Long targetUserId,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);

  /**
   * 查询未发送邮件通知的记录（targetUserId不为空且isEmailSent=false）
   */
  @Query("SELECT n FROM Notification n WHERE n.targetUserId IS NOT NULL AND n.isEmailSent = false ORDER BY n.timestamp ASC LIMIT ?1")
  List<Notification> findUnsentEmailNotifications(int size);

  /**
   * 批量更新邮件发送状态
   */
  @Modifying
  @Transactional
  @Query("UPDATE Notification n SET n.isEmailSent = :isEmailSent WHERE n.id IN :ids")
  void updateEmailSentStatus(@Param("ids") List<Long> ids,
      @Param("isEmailSent") Boolean isEmailSent);

  /**
   * 统计指定租户ID列表的通知总数
   */
  @Query(value = "SELECT COUNT(*) FROM gm_notification n WHERE n.tenant_id IN :tenantIds", nativeQuery = true)
  long countAllByTenantIdIn(@Param("tenantIds") List<Long> tenantIds);

  /**
   * 统计指定租户ID列表的站内通知数量（未发送邮件的通知）
   */
  @Query(value = "SELECT COUNT(*) FROM gm_notification n WHERE n.tenant_id IN :tenantIds AND n.is_email_sent = 0", nativeQuery = true)
  long countInternalByTenantIdIn(@Param("tenantIds") List<Long> tenantIds);

  /**
   * 统计指定租户ID列表的已发送邮件通知数量
   */
  @Query(value = "SELECT COUNT(*) FROM gm_notification n WHERE n.tenant_id IN :tenantIds AND n.is_email_sent = 1", nativeQuery = true)
  long countEmailSentByTenantIdIn(@Param("tenantIds") List<Long> tenantIds);

  /**
   * 统计指定租户ID列表在指定时间范围内的通知数量
   */
  @Query(value = "SELECT COUNT(*) FROM gm_notification n WHERE n.tenant_id IN :tenantIds AND n.timestamp BETWEEN :startTime AND :endTime", nativeQuery = true)
  long countByTenantIdInAndTimestampBetween(
      @Param("tenantIds") List<Long> tenantIds,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);
}

