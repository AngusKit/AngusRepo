package cloud.xcan.angus.core.gm.interfaces.role.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询角色授权主体请求参数")
public class RoleObjectFindDto extends PageQuery {

  @Schema(description = "授权主体（用户、部门、组）名称")
  private String name;

}
