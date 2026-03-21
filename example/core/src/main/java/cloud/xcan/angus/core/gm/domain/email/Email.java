package cloud.xcan.angus.core.gm.domain.email;

import cloud.xcan.angus.api.commonlink.email.EmailStatus;
import cloud.xcan.angus.core.gm.domain.email.enums.EmailType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Getter
@Setter
@Entity
@Table(name = "gm_email")
public class Email extends TenantAuditingEntity<Email, Long> {

  @Id
  @Column(name = "id")
  private Long id;

  @Column(name = "subject", length = 400)
  private String subject;

  @Type(JsonType.class)
  @Column(name = "to_recipients", columnDefinition = "json")
  private List<String> toRecipients;

  @Type(JsonType.class)
  @Column(name = "cc_recipients", columnDefinition = "json")
  private List<String> ccRecipients;

  @Type(JsonType.class)
  @Column(name = "bcc_recipients", columnDefinition = "json")
  private List<String> bccRecipients;

  @Column(name = "reply_to", length = 100)
  private String replyTo;

  @Column(name = "html_content", columnDefinition = "TEXT")
  private String htmlContent;

  @Column(name = "text_content", columnDefinition = "TEXT")
  private String textContent;

  @Column(name = "template_id")
  private Long templateId;

  @Type(JsonType.class)
  @Column(name = "template_params", columnDefinition = "json")
  private Map<String, Object> templateParams;

  @Type(JsonType.class)
  @Column(name = "attachments", columnDefinition = "json")
  private List<Map<String, Object>> attachments;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private EmailStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", length = 20)
  private EmailType type;

  @Column(name = "priority")
  private Integer priority;

  @Column(name = "external_id", length = 100)
  private String externalId;

  @Column(name = "send_time")
  private LocalDateTime sendTime;

  @Column(name = "deliver_time")
  private LocalDateTime deliverTime;

  @Column(name = "error_code", length = 50)
  private String errorCode;

  @Column(name = "error_message", length = 500)
  private String errorMessage;

  @Column(name = "retry_count")
  private Integer retryCount;

  @Column(name = "max_retry")
  private Integer maxRetry;

  @Override
  public Long identity() {
    return id;
  }
}
