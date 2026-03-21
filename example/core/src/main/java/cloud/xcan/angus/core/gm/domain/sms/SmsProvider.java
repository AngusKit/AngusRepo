package cloud.xcan.angus.core.gm.domain.sms;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "gm_sms_provider")
public class SmsProvider extends AuditingEntity<SmsProvider, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  /**
   * SMS service provider logo.
   */
  @Column(name = "logo", nullable = true, length = 200)
  private String logo;

  @Column(name = "is_default", nullable = false)
  private Boolean isDefault = false;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private EnabledStatus status;

  @Type(JsonType.class)
  @Column(name = "config", columnDefinition = "json")
  private Map<String, String> config;

  @Override
  public Long identity() {
    return id;
  }
}

