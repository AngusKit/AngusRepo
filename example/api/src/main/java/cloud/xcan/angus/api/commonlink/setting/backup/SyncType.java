package cloud.xcan.angus.api.commonlink.setting.backup;

import cloud.xcan.angus.spec.experimental.Value;

public enum SyncType implements Value<String> {
  FTP,     // FTP协议
  SFTP,    // SFTP协议
  S3,      // AWS S3
  OSS;     // 阿里云OSS

  @Override
  public String getValue() {
    return this.name();
  }
}
