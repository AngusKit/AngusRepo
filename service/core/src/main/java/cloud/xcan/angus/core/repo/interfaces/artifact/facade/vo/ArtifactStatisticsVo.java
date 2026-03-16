package cloud.xcan.angus.core.repo.interfaces.artifact.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "制品统计信息")
public class ArtifactStatisticsVo implements Serializable {

  @Schema(description = "制品总数")
  private Long totalArtifacts;

  @Schema(description = "总下载次数")
  private Long totalDownloads;

  @Schema(description = "总存储大小（字节）")
  private Long totalSize;

  @Schema(description = "平均文件大小（字节）")
  private Long averageSize;

  @Schema(description = "下载量最高的制品名称")
  private String topDownloaded;
}
