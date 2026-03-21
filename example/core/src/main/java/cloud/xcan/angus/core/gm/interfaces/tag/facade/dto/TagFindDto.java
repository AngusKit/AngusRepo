package cloud.xcan.angus.core.gm.interfaces.tag.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "标签查询DTO")
public class TagFindDto extends PageQuery {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "标签名称")
  private String name;

  @Schema(description = "分类ID筛选")
  private String categoryId;

  @Schema(description = "是否系统标签筛选")
  private Boolean isSystem;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
