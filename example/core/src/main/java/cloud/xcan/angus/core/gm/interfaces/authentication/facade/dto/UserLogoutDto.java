package cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "退出登录请求参数")
public class UserLogoutDto {

  @Schema(description = "访问令牌")
  private String accessToken;
}
