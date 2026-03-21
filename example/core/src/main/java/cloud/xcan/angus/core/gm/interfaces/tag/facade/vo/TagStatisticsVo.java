package cloud.xcan.angus.core.gm.interfaces.tag.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "标签统计VO")
public class TagStatisticsVo implements Serializable {

  @Schema(description = "总标签数")
  private Integer totalTags;

  @Schema(description = "系统标签数")
  private Integer systemTags;

  @Schema(description = "自定义标签数")
  private Integer customTags;

  @Schema(description = "总分类数")
  private Integer totalCategories;

  @Schema(description = "按分类统计")
  private List<CategoryTagCount> categoryStatistics;
}
