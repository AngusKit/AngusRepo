package cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "仓库对比分析结果")
public class RepositoryComparisonVo implements Serializable {

  @Schema(description = "仓库ID")
  private Long repositoryId;

  @Schema(description = "仓库名称")
  private String repositoryName;

  @Schema(description = "格式")
  private String format;

  @Schema(description = "制品数量")
  private Long artifactCount;

  @Schema(description = "下载次数")
  private Long downloadCount;

  @Schema(description = "存储大小(字节)")
  private Long storageBytes;

  @Schema(description = "活跃度评分")
  private Double activityScore;
}
