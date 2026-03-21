package cloud.xcan.angus.core.gm.domain.user.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * 密码强度枚举
 */
public enum PasswordStrength implements Value<String> {
  /**
   * 弱
   */
  WEAK,
  /**
   * 中
   */
  MEDIUM,
  /**
   * 强
   */
  STRONG;

  @Override
  public String getValue() {
    return this.name();
  }
}
