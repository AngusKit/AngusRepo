package cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "角色信息")
public class RoleInfoVo {

  @Schema(description = "角色ID")
  private String id;

  @Schema(description = "角色名称")
  private String name;

  @Schema(description = "角色编码")
  private String code;
}
