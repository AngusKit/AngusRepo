package cloud.xcan.angus.core.gm.domain.user;

import cloud.xcan.angus.api.commonlink.user.enums.InviteStatus;
import cloud.xcan.angus.api.commonlink.user.enums.InviteType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "gm_user_invite")
public class UserInvite extends TenantEntity<UserInvite, Long> {

  @Id
  private Long id;

  @Column(name = "email", length = 100)
  private String email;

  @Schema(description = "app_id")
  private Long appId;

  @Column(name = "role_id")
  private Long roleId;

  @Column(name = "department_id")
  private Long departmentId;

  @Column(name = "message")
  private String message;

  @Column(name = "invited_by")
  private Long invitedBy;

  @Enumerated(EnumType.STRING)
  @Column(name = "invite_type", nullable = false, length = 20)
  private InviteType inviteType;

  @Column(name = "invite_date", nullable = false)
  private LocalDateTime inviteDate;

  @Column(name = "expiry_date")
  private LocalDateTime expiryDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private InviteStatus status;

  @Column(name = "invite_code", nullable = false, length = 50, unique = true)
  private String inviteCode;

  @Column(name = "invite_url", length = 500)
  private String inviteUrl;

  @Override
  public Long identity() {
    return id;
  }
}
