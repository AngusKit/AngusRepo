package cloud.xcan.angus.core.gm.infra.backup;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 备份清单
 */
@Data
public class BackupManifest {

  /**
   * 备份ID
   */
  private Long backupId;

  /**
   * 备份名称
   */
  private String backupName;

  /**
   * 备份类型：FULL（全量）、INCREMENTAL（增量）
   */
  private String backupType;

  /**
   * 备份时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime backupTime;

  /**
   * 数据库类型
   */
  private String dbType;

  /**
   * 数据库备份文件路径（相对于备份根目录）
   */
  private String databaseBackupFile;

  /**
   * 上次备份ID（增量备份时使用）
   */
  private Long lastBackupId;

  /**
   * 上次备份的最大ID（增量备份时使用）
   */
  private Long lastBackupMaxId;

  /**
   * 当前备份的最大ID（增量备份时使用）
   */
  private Long currentMaxId;

  /**
   * 应用备份信息列表
   */
  private List<ApplicationBackupInfo> applications = new ArrayList<>();

  /**
   * 错误信息列表
   */
  private List<String> errors = new ArrayList<>();

  /**
   * 添加错误信息
   */
  public void addError(String error) {
    if (errors == null) {
      errors = new ArrayList<>();
    }
    errors.add(error);
  }
}
