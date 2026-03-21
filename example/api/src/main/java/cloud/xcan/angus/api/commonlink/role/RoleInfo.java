package cloud.xcan.angus.api.commonlink.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
@Schema(description = "角色信息")
public class RoleInfo {

  @Schema(description = "角色ID")
  private Long id;

  @Schema(description = "角色名称")
  private String name;

  @Schema(description = "角色编码")
  private String code;

  @Schema(description = "应用ID")
  private Long appId;

  @Schema(description = "应用名称")
  private String appName;
}
