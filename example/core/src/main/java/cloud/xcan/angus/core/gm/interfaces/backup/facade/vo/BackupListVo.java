package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import cloud.xcan.angus.core.gm.domain.backup.enums.BackupStatus;
import cloud.xcan.angus.core.gm.domain.backup.enums.BackupType;
import cloud.xcan.angus.remote.vo.AuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "备份列表项")
public class BackupListVo extends AuditingVo {

  @Schema(description = "备份ID")
  private Long id;

  @Schema(description = "备份名称")
  private String name;

  @Schema(description = "备份类型")
  private BackupType type;

  @Schema(description = "备份状态")
  private BackupStatus status;

  @Schema(description = "失败时的错误信息")
  private String errorMessage;

  @Schema(description = "文件大小（格式化的字符串，如：2.5 GB）")
  private String size;

  @Schema(description = "备份文件路径")
  private String path;

  @Schema(description = "持续时间，单位秒")
  private Long duration;

  @Schema(description = "开始时间")
  private LocalDateTime startTime;

  @Schema(description = "结束时间")
  private LocalDateTime endTime;

  @Schema(description = "文件大小（字节数）")
  private Long fileSize;

}
