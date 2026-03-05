package cloud.xcan.angus.core.repo.interfaces.cleanup.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "清理统计信息")
public class CleanupStatisticsVo {

  @Schema(description = "策略总数")
  private Long totalPolicies;

  @Schema(description = "已启用策略数")
  private Long enabledPolicies;

  @Schema(description = "执行总数")
  private Long totalExecutions;

  @Schema(description = "已完成执行数")
  private Long completedExecutions;

  @Schema(description = "失败执行数")
  private Long failedExecutions;

  @Schema(description = "已删除制品总数")
  private Long totalDeletedArtifacts;

  @Schema(description = "已释放存储空间（字节）")
  private Long totalFreedSpaceBytes;
}
