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
@Table(name = "team_member")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class TeamMember extends TenantEntity<TeamMember, Long> {

  @Id
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "avatar")
  private String avatar;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 50)
  private UserRole role;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 50)
  private MemberStatus status = MemberStatus.ACTIVE;

  @Column(name = "joined_date", nullable = false)
  private LocalDateTime joinedDate;

  @Column(name = "last_active")
  private LocalDateTime lastActive;

  @Column(name = "created_date", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
