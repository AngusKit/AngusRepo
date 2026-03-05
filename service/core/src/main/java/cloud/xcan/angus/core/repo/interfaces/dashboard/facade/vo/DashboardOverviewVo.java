package cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "仪表盘总览")
public class DashboardOverviewVo {

  @Schema(description = "仓库总数")
  private Long totalRepositories;

  @Schema(description = "制品总数")
  private Long totalArtifacts;

  @Schema(description = "总下载次数")
  private Long totalDownloads;

  @Schema(description = "总存储大小(字节)")
  private Long totalStorageBytes;

  @Schema(description = "用户总数")
  private Long totalUsers;

  @Schema(description = "活跃用户数(近30天)")
  private Long activeUsers;
}
