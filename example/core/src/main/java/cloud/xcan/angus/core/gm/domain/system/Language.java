package cloud.xcan.angus.core.gm.domain.system;

import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 支持的语言实体（系统配置）
 */
@Setter
@Getter
@Entity
@Table(name = "gm_supported_language")
public class Language extends TenantAuditingEntity<Language, Long> {

  @Id
  private Long id;

  @Column(name = "code", nullable = false, unique = true, length = 10)
  private String code;

  @Column(name = "name", nullable = false, length = 50)
  private String name;

  @Column(name = "native_name", nullable = false, length = 50)
  private String nativeName;

  @Column(name = "enabled", nullable = false)
  private Boolean enabled;

  @Column(name = "sort_order")
  private Integer sortOrder;

  @Override
  public Long identity() {
    return this.id;
  }
}
