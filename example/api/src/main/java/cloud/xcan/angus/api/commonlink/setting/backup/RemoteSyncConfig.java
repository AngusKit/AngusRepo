package cloud.xcan.angus.api.commonlink.setting.backup;

import lombok.Getter;
import lombok.Setter;

/**
 * 异地备份同步配置
 */
@Getter
@Setter
public class RemoteSyncConfig {

  /**
   * 同步类型
   */
  private SyncType syncType;

  /**
   * 远程主机地址
   */
  private String host;

  /**
   * 端口号
   */
  private Integer port;

  /**
   * 用户名
   */
  private String username;

  /**
   * 密码(加密存储)
   */
  private String password;

  /**
   * 远程路径
   */
  private String remotePath;
}
