package cloud.xcan.angus.core.repo.domain.reposettings;

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
@Table(name = "webhook_log")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class WebhookLog extends TenantEntity<WebhookLog, Long> {

  @Id
  private Long id;

  @Column(name = "webhook_id")
  private Long webhookId;

  @Column(name = "event")
  private String event;

  @Column(name = "status_code")
  private Integer statusCode;

  @Column(name = "success")
  private Boolean success;

  @Column(name = "request", columnDefinition = "TEXT")
  private String request;

  @Column(name = "response", columnDefinition = "TEXT")
  private String response;

  @Column(name = "response_time")
  private Long responseTime;

  @Column(name = "triggered_at")
  private LocalDateTime triggeredAt;

  @PrePersist
  public void prePersist() {
    if (triggeredAt == null) {
      triggeredAt = LocalDateTime.now();
    }
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
