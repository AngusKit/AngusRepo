package cloud.xcan.angus.api.gm.user.vo;

import cloud.xcan.angus.api.commonlink.SuccessStatus;
import cloud.xcan.angus.api.enums.SignInType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "登录历史")
public class LoginHistoryVo {

  @Schema(description = "登录时间")
  private LocalDateTime time;

  @Schema(description = "IP地址")
  private String ip;

  @Schema(description = "登录类型")
  private SignInType loginType;

  @Schema(description = "登录状态")
  private SuccessStatus loginStatus;

  @Schema(description = "IP地址")
  private String ipAddress;

  @Schema(description = "登录地点")
  private String location;

  @Schema(description = "设备信息")
  private String device;

  @Schema(description = "用户代理")
  private String userAgent;

  @Schema(description = "失败原因")
  private String failureReason;
}
