package cloud.xcan.angus.core.repo.domain.user;

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
@Table(name = "user_profile")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class UserProfile extends TenantEntity<UserProfile, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(name = "avatar")
  private String avatar;

  @Column(name = "role", length = 50)
  private String role;

  @Column(name = "department")
  private String department;

  @Column(name = "joined_date", nullable = false)
  private LocalDateTime joinedDate;

  @Column(name = "last_login")
  private LocalDateTime lastLogin;

  @Column(name = "preferences", columnDefinition = "JSON")
  private String preferences;

  @Column(name = "notification_settings", columnDefinition = "JSON")
  private String notificationSettings;

  @Column(name = "created_date", nullable = false, updatable = false)
  private LocalDateTime createdDate;

  @Column(name = "modified_date")
  private LocalDateTime modifiedDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
