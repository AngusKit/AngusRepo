package cloud.xcan.angus.core.repo.domain.team;

import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "team_invitation")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class TeamInvitation extends TenantEntity<TeamInvitation, Long> {

  @Id
  private Long id;

  @Column(name = "email", nullable = false)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 50)
  private UserRole role;

  @Column(name = "token", nullable = false, unique = true)
  private String token;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 50)
  private InvitationStatus status = InvitationStatus.PENDING;

  @Column(name = "message", columnDefinition = "TEXT")
  private String message;

  @Column(name = "invited_by", nullable = false)
  private Long invitedBy;

  @Column(name = "invited_date", nullable = false)
  private LocalDateTime invitedDate;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Column(name = "accepted_date")
  private LocalDateTime acceptedDate;

  @Column(name = "created_by", nullable = false)
  private Long createdBy;

  @Column(name = "created_date", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
