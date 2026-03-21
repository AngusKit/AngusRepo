package cloud.xcan.angus.core.gm.domain.sms;

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
import jakarta.persistence.UniqueConstraint;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Getter
@Setter
@Entity
@Table(name = "gm_sms_template", uniqueConstraints = {
    @UniqueConstraint(name = "uk_provider_code_language", columnNames = {"provider", "code",
        "language"})
})
public class SmsTemplate extends AuditingEntity<SmsTemplate, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "code", nullable = false, length = 80)
  private String code;

  @Enumerated(EnumType.STRING)
  @Column(name = "language", nullable = false, length = 40)
  private Language language;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Type(JsonType.class)
  @Column(name = "params", columnDefinition = "json")
  private List<String> params;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private EnabledStatus status;

  @Column(name = "provider", length = 50, nullable = false)
  private String provider;

  /**
   * 服务商模板编码
   */
  @Column(name = "template_code", length = 100)
  private String templateCode;

  @Column(name = "signature", length = 100)
  private String signature;

  @Transient
  private Long usageCount;

  @Override
  public Long identity() {
    return id;
  }
}

