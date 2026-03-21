package cloud.xcan.angus.core.gm.interfaces.group.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询组成员请求参数")
public class GroupUserFindDto extends PageQuery {

  @Schema(description = "用户ID")
  private Long id;

  @Schema(description = "用户名称")
  private String name;
}
