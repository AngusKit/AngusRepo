package cloud.xcan.angus.core.gm.domain.email;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Setter
@Getter
@Entity
@Table(name = "gm_email_template")
public class EmailTemplate extends AuditingEntity<EmailTemplate, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "code", nullable = false, length = 80)
  private String code;

  @Enumerated(EnumType.STRING)
  @Column(name = "language", nullable = false, length = 20)
  private Language language;

  @Column(name = "subject", nullable = false, length = 200)
  private String subject;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Type(JsonType.class)
  @Column(name = "params", columnDefinition = "json")
  private List<String> params;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private EnabledStatus status;

  @Column(name = "is_system", nullable = false)
  private Boolean isSystem = false;

  @Transient
  private Long usageCount;
  @Transient
  private Double openRate;
  @Transient
  private Double clickRate;

  @Override
  public Long identity() {
    return id;
  }
}

