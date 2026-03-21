package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import cloud.xcan.angus.core.gm.domain.backup.enums.BackupType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "备份文件验证结果")
public class BackupValidationVo {

  @Schema(description = "是否有效")
  private Boolean valid;

  @Schema(description = "文件名")
  private String fileName;

  @Schema(description = "文件大小")
  private String fileSize;

  @Schema(description = "备份类型")
  private BackupType backupType;

  @Schema(description = "备份日期")
  private String backupDate;

  @Schema(description = "应用名称列表")
  private List<String> appNames;

  @Schema(description = "文件校验和")
  private String checksum;

  @Schema(description = "是否兼容当前系统版本")
  private Boolean compatible;

  @Schema(description = "验证信息或警告")
  private List<String> messages;
}
