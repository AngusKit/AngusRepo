package cloud.xcan.angus.core.repo.domain.access;

import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "access_log")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class AccessLog extends TenantEntity<AccessLog, Long> {

  @Id
  private Long id;

  @Column(name = "repository_id", nullable = false)
  private Long repositoryId;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "permission")
  private String permission;

  @Column(name = "path")
  private String path;

  @Column(name = "ip_address")
  private String ipAddress;

  @Column(name = "user_agent")
  private String userAgent;

  @Column(name = "success")
  private Boolean success;

  @Column(name = "error_message")
  private String errorMessage;

  @Column(name = "access_time")
  private LocalDateTime accessTime;

  @PrePersist
  public void prePersist() {
    if (accessTime == null) {
      accessTime = LocalDateTime.now();
    }
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
