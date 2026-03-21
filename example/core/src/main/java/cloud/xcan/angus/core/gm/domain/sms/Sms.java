package cloud.xcan.angus.core.gm.domain.sms;

import cloud.xcan.angus.api.commonlink.sms.SmsStatus;
import cloud.xcan.angus.core.gm.domain.sms.enums.SmsType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Getter
@Setter
@Entity
@Table(name = "gm_sms")
public class Sms extends TenantAuditingEntity<Sms, Long> {

  @Id
  private Long id;

  @Column(name = "phone", length = 20, nullable = false)
  private String phone;

  @Column(name = "template_id")
  private Long templateId;

  @Column(name = "message_id", length = 100)
  private String messageId;

  @Column(name = "content", length = 1000, nullable = false)
  private String content;

  @Column(name = "template_code", length = 50)
  private String templateCode;

  @Type(JsonType.class)
  @Column(name = "template_params", columnDefinition = "json")
  private Map<String, String> templateParams;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20, nullable = false)
  private SmsStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", length = 20, nullable = false)
  private SmsType type;

  @Column(name = "provider", length = 50)
  private String provider;

  @Column(name = "send_time")
  private LocalDateTime sendTime;

  @Column(name = "deliver_time")
  private LocalDateTime deliverTime;

  @Column(name = "error_code", length = 50)
  private String errorCode;

  @Column(name = "error_message", length = 500)
  private String errorMessage;

  @Column(name = "external_id", length = 100)
  private String externalId;

  @Column(name = "retry_count")
  private Integer retryCount;

  @Column(name = "max_retry")
  private Integer maxRetry;

  @Override
  public Long identity() {
    return id;
  }
}
