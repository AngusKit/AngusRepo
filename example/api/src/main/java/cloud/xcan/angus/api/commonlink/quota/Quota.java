package cloud.xcan.angus.api.commonlink.quota;

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
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 资源配额实体
 */
@Getter
@Setter
@Entity
@Table(name = "gm_quota")
@EntityListeners({AuditingEntityListener.class, TenantListener.class})
public class Quota extends TenantEntity<Quota, Long> {

  @Id
  private Long id;

  @Column(name = "code", length = 80, nullable = false)
  private String code;

  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Column(name = "app_code", length = 80, nullable = false)
  private String appCode;

  @Column(name = "limit_value", nullable = false)
  private Long limit;

  @Column(name = "used_value", nullable = false)
  private Long used = 0L;

  @Column(name = "unit", length = 50, nullable = false)
  private String unit;

  @Column(name = "description", length = 400)
  private String description;

  @Column(name = "icon", length = 100)
  private String icon;

  @Column(name = "enabled", nullable = false)
  private Boolean enabled = true;

  /**
   * 是否许可控制
   * <p>
   * 如果为true，表示该配额由许可（License）控制，不允许通过系统界面修改。 许可控制的配额值由许可系统自动管理，用户只能查看，不能编辑。
   * </p>
   */
  @Column(name = "is_license_control", nullable = false)
  private Boolean isLicenseControl = false;

  /**
   * 是否初始化模板
   * <p>
   * 如果为true，表示该配额是初始化模板，用于新租户配额配置初始化。当创建新租户时，系统会根据模板配额自动创建对应的配额配置。
   * </p>
   */
  @Column(name = "is_init_template", nullable = false)
  private Boolean isInitTemplate = false;

  @LastModifiedDate
  @Column(name = "modified_date", columnDefinition = "TIMESTAMP")
  protected LocalDateTime modifiedDate;

  @LastModifiedBy
  @Column(name = "modified_by")
  protected Long modifiedBy;

  @Override
  public Long identity() {
    return id;
  }
}
