package cloud.xcan.angus.core.gm.domain.log.enums;

import cloud.xcan.angus.spec.experimental.Value;

/**
 * 用户操作类型枚举
 */
public enum OperationAction implements Value<String> {
  /**
   * 读取
   */
  READ,

  /**
   * 创建
   */
  CREATE,

  /**
   * 修改
   */
  UPDATE,

  /**
   * 删除
   */
  DELETE;

  @Override
  public String getValue() {
    return name();
  }
}
