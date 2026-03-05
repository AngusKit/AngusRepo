package cloud.xcan.angus.core.repo.interfaces.system.facade.dto;

import cloud.xcan.angus.core.repo.domain.system.ConnectionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "连接测试请求参数")
public class ConnectionTestDto {

  @NotNull
  @Schema(description = "连接类型", requiredMode = Schema.RequiredMode.REQUIRED)
  private ConnectionType type;

  @Schema(description = "连接配置（JSON）")
  private String config;
}
