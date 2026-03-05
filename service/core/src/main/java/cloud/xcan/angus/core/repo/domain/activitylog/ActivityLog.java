package cloud.xcan.angus.core.repo.domain.activitylog;

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

/**
 * 活动日志实体
 */
@Entity
@Table(name = "activity_log")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class ActivityLog extends TenantEntity<ActivityLog, String> {

  @Id
  private String id;

  @Enumerated(EnumType.STRING)
  @Column(name = "action", nullable = false, length = 50)
  private ActivityAction action;

  @Column(name = "user", nullable = false, length = 255)
  private String user;

  @Column(name = "artifact", nullable = false, length = 500)
  private String artifact;

  @Column(name = "repository", nullable = false, length = 255)
  private String repository;

  @Column(name = "timestamp", nullable = false)
  private LocalDateTime timestamp;

  @Column(name = "ip_address", length = 50)
  private String ipAddress;

  @Column(name = "user_agent", length = 500)
  private String userAgent;

  @Column(name = "details", columnDefinition = "TEXT")
  private String details;

  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false, length = 50)
  private ActivityCategory category;

  @Override
  public String identity() {
    return this.id;
  }
}
