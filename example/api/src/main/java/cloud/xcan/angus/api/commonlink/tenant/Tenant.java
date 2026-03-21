package cloud.xcan.angus.api.commonlink.tenant;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.api.commonlink.tenant.enums.AccountType;
import cloud.xcan.angus.api.commonlink.tenant.enums.TenantType;
import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "gm_tenant")
public class Tenant extends AuditingEntity<Tenant, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "code", nullable = false, length = 40, unique = true)
  private String code;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", length = 20)
  private TenantType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "account_type", length = 20)
  private AccountType accountType;

  @Column(name = "main_tenant_id")
  private Long mainTenantId;

  @Column(name = "admin_name", length = 100)
  private String adminName;

  @Column(name = "admin_email", length = 100)
  private String adminEmail;

  @Column(name = "admin_phone", length = 20)
  private String adminPhone;

  @Column(name = "address", length = 500)
  private String address;

  @Column(name = "expire_date")
  private LocalDateTime expireDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 10)
  private EnabledStatus status;

  @Column(name = "logo", length = 400)
  private String logo;

  @Enumerated(EnumType.STRING)
  @Column(name = "default_language", length = 10)
  private Language defaultLanguage;

  // Non-persistent fields - for temporary associated data
  @Transient
  private Long userCount;
  @Transient
  private Long departmentCount;
  @Transient
  private Long subTenantCount;

  @Override
  public Long identity() {
    return id;
  }
}
