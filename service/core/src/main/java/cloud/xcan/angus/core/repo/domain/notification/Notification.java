package cloud.xcan.angus.core.repo.domain.notification;

import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "notification")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class Notification extends TenantEntity<Notification, String> {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationPriority priority = NotificationPriority.MEDIUM;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "is_starred", nullable = false)
    private Boolean isStarred = false;

    @Column(name = "is_archived", nullable = false)
    private Boolean isArchived = false;

    @Column(name = "target_user_id", nullable = false)
    private Long targetUserId;

    @Column(name = "source_id", length = 64)
    private String sourceId;

    @Column(name = "source_type", length = 50)
    private String sourceType;

    @Column(name = "action_url", length = 1000)
    private String actionUrl;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "read_date")
    private LocalDateTime readDate;

    @Override
    public String identity() {
        return this.id;
    }
}
