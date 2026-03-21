package cloud.xcan.angus.api.commonlink.user.enums;

import cloud.xcan.angus.spec.experimental.Value;

public enum InviteStatus implements Value<String> {
  PENDING,    // 待接受
  EXPIRED,    // 已过期
  ACCEPTED,   // 已接受
  CANCELLED,  // 已取消
  REJECTED;   // 已拒绝

  @Override
  public String getValue() {
    return this.name();
  }
}
