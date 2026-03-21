package cloud.xcan.angus.core.gm.infra.backup;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 应用备份信息
 */
@Data
public class ApplicationBackupInfo {

  /**
   * 应用ID
   */
  private Long applicationId;

  /**
   * 应用名称
   */
  private String applicationName;

  /**
   * 应用编码
   */
  private String applicationCode;

  /**
   * 应用安装路径
   */
  private String installedPath;

  /**
   * 是否备份了conf目录
   */
  private Boolean confBackedUp = false;

  /**
   * conf目录备份路径（相对于备份根目录）
   */
  private String confPath;

  /**
   * 是否备份了data目录
   */
  private Boolean dataBackedUp = false;

  /**
   * data目录备份路径（相对于备份根目录）
   */
  private String dataPath;

  /**
   * 是否备份了logs目录
   */
  private Boolean logsBackedUp = false;

  /**
   * logs目录备份路径（相对于备份根目录）
   */
  private String logsPath;

  /**
   * 文件元数据索引列表 记录每个备份文件的ID信息，用于增量备份判断
   */
  private List<FileMetadata> fileMetadataIndex = new ArrayList<>();
}
