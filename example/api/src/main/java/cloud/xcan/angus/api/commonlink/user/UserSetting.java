package cloud.xcan.angus.api.commonlink.user;

import cloud.xcan.angus.api.commonlink.user.enums.UserSettingKey;
import cloud.xcan.angus.api.commonlink.user.model.UserSettingValue;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
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
import org.hibernate.annotations.Type;

@Entity
@Table(name = "gm_user_setting")
@Setter
@Getter
@Accessors(chain = true)
public class UserSetting extends TenantEntity<UserSetting, Long> {

  @Id
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "setting_key", nullable = false, length = 50)
  private UserSettingKey key;

  @Column(name = "value", columnDefinition = "json")
  @Type(JsonType.class)
  private UserSettingValue value;

  // ========== OAuth第三方账号绑定字段 ==========

  /**
   * 微信用户ID
   */
  @Column(name = "wechat_user_id", length = 100)
  private String wechatUserId;

  /**
   * 微信绑定时间
   */
  @Column(name = "wechat_user_bind_date")
  private LocalDateTime wechatUserBindDate;

  /**
   * GitHub用户ID
   */
  @Column(name = "github_user_id", length = 100)
  private String githubUserId;

  /**
   * GitHub绑定时间
   */
  @Column(name = "github_user_bind_date")
  private LocalDateTime githubUserBindDate;

  /**
   * Google用户ID
   */
  @Column(name = "google_user_id", length = 100)
  private String googleUserId;

  /**
   * Google绑定时间
   */
  @Column(name = "google_user_bind_date")
  private LocalDateTime googleUserBindDate;

  @Override
  public Long identity() {
    return id;
  }
}
