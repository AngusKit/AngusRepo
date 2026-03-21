package cloud.xcan.angus.core.gm.interfaces.user.facade.vo;

import cloud.xcan.angus.api.enums.DeviceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "登录设备信息")
public class LoginDeviceVo {

  @Schema(description = "设备ID")
  private Long id;

  @Schema(description = "用户ID")
  private Long userId;

  @Schema(description = "设备名称")
  private String deviceName;

  @Schema(description = "设备类型")
  private DeviceType deviceType;

  @Schema(description = "浏览器")
  private String browser;

  @Schema(description = "浏览器版本")
  private String browserVersion;

  @Schema(description = "操作系统")
  private String os;

  @Schema(description = "操作系统版本")
  private String osVersion;

  @Schema(description = "IP地址")
  private String ipAddress;

  @Schema(description = "地理位置")
  private String location;

  @Schema(description = "是否当前设备")
  private Boolean isCurrent;

  @Schema(description = "最后活跃时间")
  private LocalDateTime lastActiveAt;
}
