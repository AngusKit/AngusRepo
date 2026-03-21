package cloud.xcan.angus.core.gm.interfaces.tag.facade.vo;

import cloud.xcan.angus.remote.vo.AuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "标签分类VO")
public class TagCategoryVo extends AuditingVo {

  @Schema(description = "分类ID")
  private Long id;

  @Schema(description = "分类编码")
  private String code;

  @Schema(description = "分类名称")
  private String name;

  @Schema(description = "分类描述")
  private String description;

  @Schema(description = "是否系统分类")
  private Boolean isSystem;

  @Schema(description = "包含的标签数量")
  private Integer tagCount;

}
