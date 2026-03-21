package cloud.xcan.angus.core.gm.infra.backup;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 文件元数据索引 用于记录备份文件中每个文件的ID信息，支持增量备份
 */
@Data
public class FileMetadata {

  /**
   * 文件相对路径（相对于应用安装目录）
   */
  private String relativePath;

  /**
   * 文件完整路径
   */
  private String fullPath;

  /**
   * 文件大小（字节）
   */
  private Long fileSize;

  /**
   * 文件修改时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime lastModifiedTime;

  /**
   * 文件哈希值（MD5或SHA256），用于判断文件是否变更
   */
  private String fileHash;

  /**
   * 文件中包含的最大ID（如果文件内容包含ID字段） 用于增量备份判断
   */
  private Long maxIdInFile;

  /**
   * 文件中包含的最小ID（如果文件内容包含ID字段）
   */
  private Long minIdInFile;

  /**
   * 文件中包含的所有ID列表（可选，仅当文件较小时记录）
   */
  private java.util.List<Long> idsInFile;

  /**
   * 文件类型（JSON, XML, TXT, BINARY等）
   */
  private String fileType;

  /**
   * 备份时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime backupTime;
}
