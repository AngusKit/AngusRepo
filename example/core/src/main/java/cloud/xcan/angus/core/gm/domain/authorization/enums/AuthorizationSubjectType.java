package cloud.xcan.angus.core.gm.domain.authorization.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * 授权主体类型枚举
 */
public enum AuthorizationSubjectType implements Value<String> {
  /**
   * 用户
   */
  USER,

  /**
   * 部门
   */
  DEPARTMENT,

  /**
   * 组
   */
  GROUP;

  @Override
  public String getValue() {
    return this.name();
  }

  public boolean isUser() {
    return this.equals(USER);
  }
}
