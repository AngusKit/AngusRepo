package cloud.xcan.angus.api.commonlink.user;

import cloud.xcan.angus.api.commonlink.user.enums.UserStatus;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Note: user is mysql keyword. Renaming to avoid providing separate repo implementations for
 * postgres.
 */
@Entity
@Table(name = "gm_user")
@Setter
@Getter
@Accessors(chain = true)
public class UserBase extends TenantEntity<UserBase, Long> {

  @Id
  private Long id;

  private String username;

  private String name;

  private String phone;

  private String email;

  private String avatar;

  @Enumerated(EnumType.STRING)
  private UserStatus status;

  private Boolean locked;

  @Column(name = "online_date")
  private LocalDateTime onlineDate;

  public UserInfo toUserInfo() {
    return new UserInfo().setId(id).setName(name).setAvatar(avatar)
        .setEmail(email).setPhone(phone);
  }

  @Override
  public Long identity() {
    return this.id;
  }
}
