package cloud.xcan.angus.api.commonlink.setting.backup;

import cloud.xcan.angus.spec.experimental.Value;

public enum CompressionLevel implements Value<String> {
  NONE,       // 无压缩
  FAST,       // 快速压缩
  STANDARD,   // 标准压缩
  MAXIMUM;    // 最大压缩

  @Override
  public String getValue() {
    return this.name();
  }
}
