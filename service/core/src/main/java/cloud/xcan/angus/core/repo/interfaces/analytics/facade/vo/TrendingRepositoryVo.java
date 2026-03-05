package cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "热门仓库")
public class TrendingRepositoryVo {

  @Schema(description = "仓库ID")
  private Long id;

  @Schema(description = "仓库名称")
  private String name;

  @Schema(description = "格式")
  private String format;

  @Schema(description = "制品数量")
  private Long artifactCount;

  @Schema(description = "下载次数")
  private Long downloadCount;

  @Schema(description = "增长率(%)")
  private Double growthRate;
}
