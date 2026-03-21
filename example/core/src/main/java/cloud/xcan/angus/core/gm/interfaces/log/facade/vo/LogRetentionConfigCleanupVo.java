package cloud.xcan.angus.core.gm.interfaces.log.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "日志清理任务结果")
public class LogRetentionConfigCleanupVo {

  @Schema(description = "清理任务ID")
  private String jobId;

  @Schema(description = "任务状态枚举值")
  private String status;

  @Schema(description = "开始时间")
  private LocalDateTime startTime;

  @Schema(description = "结束时间")
  private LocalDateTime endTime;

  @Schema(description = "执行时长")
  private String duration;

  @Schema(description = "清理结果")
  private CleanupResultVo result;

  @Schema(description = "错误列表（如有）")
  private List<String> errors;

  @Data
  @Schema(description = "清理结果详情")
  public static class CleanupResultVo {

    @Schema(description = "删除的用户日志数")
    private Long userLogsDeleted;

    @Schema(description = "删除的系统日志数")
    private Long systemLogsDeleted;

    @Schema(description = "删除的API日志数")
    private Long apiLogsDeleted;

    @Schema(description = "总删除记录数")
    private Long totalRecordsDeleted;

    @Schema(description = "释放空间大小（字节）")
    private Long totalSizeFreed;

    @Schema(description = "格式化释放空间")
    private String totalSizeFreedFormatted;
  }
}
