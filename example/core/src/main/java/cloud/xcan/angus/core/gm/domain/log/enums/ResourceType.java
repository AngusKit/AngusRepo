package cloud.xcan.angus.core.gm.domain.log.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * 操作资源类型枚举
 */
public enum ResourceType implements Value<String> {
  /**
   * 用户
   */
  USER,

  /**
   * 租户
   */
  TENANT,

  /**
   * 组织（部门、组）
   */
  ORGANIZATION,

  /**
   * 权限（角色、授权）
   */
  PERMISSION,

  /**
   * 应用（应用菜单、应用功能）
   */
  APPLICATION,

  /**
   * 配置
   */
  CONFIG,

  /**
   * 配额
   */
  QUOTA,

  /**
   * 系统事件
   */
  SYSTEM_EVENT,

  /**
   * 其他
   */
  OTHER;

  @Override
  public String getValue() {
    return name();
  }
}
