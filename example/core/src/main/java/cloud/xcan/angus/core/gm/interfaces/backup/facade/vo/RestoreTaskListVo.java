package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import cloud.xcan.angus.core.gm.domain.backup.enums.RestoreSource;
import cloud.xcan.angus.core.gm.domain.backup.enums.RestoreStatus;
import cloud.xcan.angus.remote.vo.AuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "恢复任务列表项")
public class RestoreTaskListVo extends AuditingVo {

  @Schema(description = "恢复任务ID")
  private Long id;

  @Schema(description = "恢复源类型")
  private RestoreSource source;

  @Schema(description = "备份ID")
  private Long backupId;

  @Schema(description = "备份名称")
  private String backupName;

  @Schema(description = "文件路径")
  private String filePath;

  @Schema(description = "恢复状态")
  private RestoreStatus status;

  @Schema(description = "开始时间")
  private LocalDateTime startTime;

  @Schema(description = "结束时间")
  private LocalDateTime endTime;

  @Schema(description = "耗时，单位秒")
  private Long duration;

  @Schema(description = "进度百分比(0-100)")
  private Integer progress;
}
