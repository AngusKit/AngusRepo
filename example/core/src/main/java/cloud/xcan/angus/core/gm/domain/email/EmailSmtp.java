package cloud.xcan.angus.core.gm.domain.email;

import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "gm_email_smtp")
public class EmailSmtp extends AuditingEntity<EmailSmtp, Long> {

  @Id
  private Long id;

  @Column(name = "host", nullable = false, length = 200)
  private String host;

  @Column(name = "port", nullable = false)
  private Integer port;

  @Column(name = "username", nullable = false, length = 100)
  private String username;

  @Column(name = "password", length = 255)
  private String password;

  @Column(name = "from_name", length = 100)
  private String fromName;

  @Column(name = "from_email", nullable = false, length = 100)
  private String fromEmail;

  @Column(name = "use_ssl")
  private Boolean useSsl = true;

  @Column(name = "use_starttls")
  private Boolean useStartTls = false;

  @Column(name = "is_default")
  private Boolean isDefault = false;

  @Override
  public Long identity() {
    return id;
  }
}

