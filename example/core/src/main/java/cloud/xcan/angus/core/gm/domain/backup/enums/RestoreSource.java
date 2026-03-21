package cloud.xcan.angus.core.gm.domain.backup.enums;

import cloud.xcan.angus.spec.experimental.Value;

public enum RestoreSource implements Value<String> {
  BACKUP,      // 从备份列表选择
  FILE_PATH;   // 指定文件路径

  @Override
  public String getValue() {
    return this.name();
  }
}
