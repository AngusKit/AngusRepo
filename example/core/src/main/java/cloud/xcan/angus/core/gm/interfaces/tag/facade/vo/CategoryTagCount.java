package cloud.xcan.angus.core.gm.interfaces.tag.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Data
@Schema(description = "分类标签统计")
public class CategoryTagCount implements Serializable {

  @Schema(description = "分类ID")
  private Long categoryId;

  @Schema(description = "分类名称")
  private String categoryName;

  @Schema(description = "标签数量")
  private Integer tagCount;
}
