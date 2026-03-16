package cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "热门制品")
public class TrendingArtifactVo implements Serializable {

  @Schema(description = "制品ID")
  private Long id;

  @Schema(description = "制品名称")
  private String name;

  @Schema(description = "仓库名称")
  private String repositoryName;

  @Schema(description = "版本")
  private String version;

  @Schema(description = "下载次数")
  private Long downloadCount;

  @Schema(description = "收藏数")
  private Integer stars;

  @Schema(description = "增长率(%)")
  private Double growthRate;
}
