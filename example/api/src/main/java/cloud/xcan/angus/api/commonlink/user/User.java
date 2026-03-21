package cloud.xcan.angus.api.commonlink.user;

import cloud.xcan.angus.api.commonlink.department.DepartmentInfo;
import cloud.xcan.angus.api.commonlink.group.GroupInfo;
import cloud.xcan.angus.api.commonlink.oauthuser.AuthenticationUser;
import cloud.xcan.angus.api.commonlink.role.RoleInfo;
import cloud.xcan.angus.api.commonlink.user.enums.UserSource;
import cloud.xcan.angus.api.commonlink.user.enums.UserStatus;
import cloud.xcan.angus.api.enums.Gender;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "gm_user")
public class User extends TenantAuditingEntity<User, Long> {

  @Id
  private Long id;

  @Column(name = "username", nullable = false, length = 100, unique = true)
  private String username;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "email", length = 100)
  private String email;

  @Column(name = "email_verified")
  private Boolean emailVerified = false;

  @Column(name = "phone", length = 20)
  private String phone;

  @Column(name = "phone_verified")
  private Boolean phoneVerified = false;

  @Column(name = "avatar", length = 500)
  private String avatar;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender", length = 20)
  private Gender gender;

  @Column(name = "landline", length = 20)
  private String landline;

  @Column(name = "bio", length = 200)
  private String bio;

  @Column(name = "job_title", length = 100)
  private String jobTitle;

  @Column(name = "location", length = 100)
  private String location; // 地区

  @Column(name = "address", length = 200)
  private String address;

  @Column(name = "website", length = 200)
  private String website;

  @Column(name = "github", length = 100)
  private String github;

  @Column(name = "twitter", length = 100)
  private String twitter;

  @Column(name = "linkedin", length = 100)
  private String linkedin;

  @Column(name = "department_id")
  private Long departmentId; // 主部门

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private UserStatus status;

  @Column(name = "sys_admin", nullable = false)
  private Boolean sysAdmin; // 租户管理员

  @Column(name = "locked", nullable = false)
  private Boolean locked;

  @Enumerated(EnumType.STRING)
  @Column(name = "source", length = 40, nullable = false)
  private UserSource source;

  @Column(name = "ldap_id")
  private Long ldapId; // 关联的LDAP配置ID

  private Boolean online;

  @Column(name = "online_date")
  private LocalDateTime onlineDate;

  @Column(name = "offline_date")
  private LocalDateTime offlineDate;

  // Non-persistent fields - for temporary associated data
  @Transient
  private String password;
  @Transient
  private List<Long> roleIds;
  @Transient
  private Map<String, String> tokenResult;
  @Transient
  private AuthenticationUser authUser;
  @Transient
  private List<RoleInfo> roles;
  @Transient
  private List<DepartmentInfo> departments;
  @Transient
  private List<GroupInfo> groups;
  @Transient
  private Boolean groupOwner;
  @Transient
  private LocalDateTime groupJoinDate;
  @Transient
  private LocalDateTime lastLogin;

  public UserInfo toUserInfo() {
    return new UserInfo().setId(id).setName(name).setAvatar(avatar)
        .setEmail(email).setPhone(phone);
  }

  @Override
  public Long identity() {
    return id;
  }
}
