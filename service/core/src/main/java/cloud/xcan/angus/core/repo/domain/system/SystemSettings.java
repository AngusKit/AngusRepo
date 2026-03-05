package cloud.xcan.angus.core.repo.domain.system;

import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
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
@Table(name = "system_settings")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class SystemSettings extends TenantEntity<SystemSettings, Long> {

  @Id
  private Long id;

  @Column(name = "setting_key", nullable = false, unique = true)
  private String settingKey;

  @Column(name = "setting_value", columnDefinition = "TEXT")
  private String settingValue;

  @Column(name = "value_type", length = 50)
  private String valueType;

  @Column(name = "encrypted")
  private Boolean encrypted = false;

  @Column(name = "modified_by")
  private Long modifiedBy;

  @Column(name = "modified_date")
  private LocalDateTime modifiedDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
