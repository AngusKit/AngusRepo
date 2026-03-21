package cloud.xcan.angus.core.gm.domain.email;

import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "gm_email_tracking")
public class EmailTracking extends TenantAuditingEntity<EmailTracking, Long> {

  @Id
  @Column(name = "id")
  private Long id;

  @Column(name = "email_id", nullable = false)
  private Long emailId;

  @Column(name = "opened")
  private Boolean opened = false;

  @Column(name = "opened_time")
  private LocalDateTime openedTime;

  @Column(name = "open_count")
  private Integer openCount = 0;

  @Column(name = "clicked")
  private Boolean clicked = false;

  @Column(name = "click_count")
  private Integer clickCount = 0;

  @Column(name = "bounced")
  private Boolean bounced = false;

  @Column(name = "bounced_time")
  private LocalDateTime bouncedTime;

  @Column(name = "complained")
  private Boolean complained = false;

  @Column(name = "complained_time")
  private LocalDateTime complainedTime;

  @Override
  public Long identity() {
    return id;
  }
}
