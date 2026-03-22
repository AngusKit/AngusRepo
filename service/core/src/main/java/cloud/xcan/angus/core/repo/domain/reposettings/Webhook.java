package cloud.xcan.angus.core.repo.domain.reposettings;

import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "webhook")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class Webhook extends TenantAuditingEntity<Webhook, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "url", nullable = false)
  private String url;

  @Column(name = "secret")
  private String secret;

  @Column(name = "active")
  private Boolean active = true;

  @Column(name = "last_trigger_time")
  private LocalDateTime lastTriggerTime;

  @Column(name = "success_count")
  private Integer successCount = 0;

  @Column(name = "failure_count")
  private Integer failureCount = 0;

  @Column(name = "events", columnDefinition = "JSON")
  private String events;

  @Override
  public Long identity() {
    return this.id;
  }
}
