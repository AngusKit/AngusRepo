package cloud.xcan.angus.core.gm.domain.user.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * OAuth第三方登录提供商枚举
 */
public enum OAuthProvider implements Value<String> {
  /**
   * 微信
   */
  WECHAT,

  /**
   * GitHub
   */
  GITHUB,

  /**
   * Google
   */
  GOOGLE;

  @Override
  public String getValue() {
    return this.name();
  }
}
