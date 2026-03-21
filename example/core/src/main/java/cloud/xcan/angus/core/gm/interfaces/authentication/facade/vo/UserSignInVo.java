package cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo;

import cloud.xcan.angus.api.commonlink.user.UserInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "登录响应")
public class UserSignInVo {

  @JsonProperty("accessToken")
  @Schema(description = "访问令牌")
  private String accessToken;

  @JsonProperty("refreshToken")
  @Schema(description = "刷新令牌")
  private String refreshToken;

  @JsonProperty("tokenType")
  @Schema(description = "令牌类型")
  private String tokenType;

  @JsonProperty("expiresIn")
  @Schema(description = "过期时间（秒）")
  private Integer expiresIn;

  @Schema(description = "用户信息")
  private UserInfo user;
}
