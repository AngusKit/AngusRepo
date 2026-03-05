package cloud.xcan.angus.core.repo.interfaces.upload.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "上传统计信息")
public class UploadStatisticsVo {

  @Schema(description = "任务总数")
  private Long totalTasks;

  @Schema(description = "待处理任务数")
  private Long pendingTasks;

  @Schema(description = "已完成任务数")
  private Long completedTasks;

  @Schema(description = "失败任务数")
  private Long failedTasks;

  @Schema(description = "总上传字节数")
  private Long totalUploadedBytes;
}
