package cloud.xcan.angus.core.repo.domain.notification;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface NotificationRepo extends BaseRepository<Notification, String> {

    Optional<Notification> findByTenantIdAndId(String tenantId, String id);

    List<Notification> findByTenantIdAndTargetUserId(String tenantId, Long targetUserId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.tenantId = :tenantId AND n.targetUserId = :userId AND n.isRead = false")
    Long countUnread(@Param("tenantId") String tenantId, @Param("userId") Long userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.tenantId = :tenantId AND n.targetUserId = :userId")
    Long countTotal(@Param("tenantId") String tenantId, @Param("userId") Long userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.tenantId = :tenantId AND n.targetUserId = :userId AND n.type = :type")
    Long countByType(@Param("tenantId") String tenantId, @Param("userId") Long userId, @Param("type") NotificationType type);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readDate = CURRENT_TIMESTAMP WHERE n.tenantId = :tenantId AND n.id = :id")
    void markAsRead(@Param("tenantId") String tenantId, @Param("id") String id);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readDate = CURRENT_TIMESTAMP WHERE n.tenantId = :tenantId AND n.id IN :ids")
    void markBatchAsRead(@Param("tenantId") String tenantId, @Param("ids") List<String> ids);

    @Modifying
    @Query("UPDATE Notification n SET n.isStarred = :starred WHERE n.tenantId = :tenantId AND n.id = :id")
    void updateStarred(@Param("tenantId") String tenantId, @Param("id") String id, @Param("starred") Boolean starred);

    @Modifying
    @Query("UPDATE Notification n SET n.isArchived = :archived WHERE n.tenantId = :tenantId AND n.id = :id")
    void updateArchived(@Param("tenantId") String tenantId, @Param("id") String id, @Param("archived") Boolean archived);
}
