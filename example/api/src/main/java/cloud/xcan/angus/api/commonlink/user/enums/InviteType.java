package cloud.xcan.angus.api.commonlink.user.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * 邀请方式枚举
 */
public enum InviteType implements Value<String> {
  /**
   * 链接邀请
   */
  LINK,
  /**
   * 邮件邀请
   */
  EMAIL;

  public boolean isEmail() {
    return this.equals(EMAIL);
  }

  @Override
  public String getValue() {
    return this.name();
  }
}
