package cloud.xcan.angus.core.gm.interfaces.role.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设置默认角色请求参数")
public class RoleDefaultDto {

  @Schema(description = "是否设为默认角色")
  private Boolean isDefault;
}
