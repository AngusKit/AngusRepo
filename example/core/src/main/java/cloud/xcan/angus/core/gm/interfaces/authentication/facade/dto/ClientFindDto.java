package cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * OAuth2客户端查询DTO
 */
@Setter
@Getter
@Accessors(chain = true)
public class ClientFindDto {

  @Schema(description = "认证客户端标识符")
  private String id;

  @Schema(description = "OAuth2客户端标识符，用于过滤")
  private String clientId;

  @Schema(description = "租户标识符，用于过滤")
  private String tenantId;

}
