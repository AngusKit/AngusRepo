package cloud.xcan.angus.core.gm.interfaces.tag.facade.vo;

import cloud.xcan.angus.remote.vo.AuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "标签列表项")
public class TagListVo extends AuditingVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "标签名称")
  private String name;

  @Schema(description = "标签描述")
  private String description;

  @Schema(description = "所属分类ID")
  private String categoryId;

  @Schema(description = "所属分类名称，冗余字段")
  private String categoryName;

  @Schema(description = "是否系统标签")
  private Boolean isSystem;

}
