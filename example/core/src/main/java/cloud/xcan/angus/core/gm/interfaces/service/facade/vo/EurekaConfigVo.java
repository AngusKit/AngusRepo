package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "Eureka配置")
public class EurekaConfigVo {

  @Schema(description = "服务URL")
  private String serviceUrl;

  @Schema(description = "是否启用认证")
  private Boolean enableAuth;

  @Schema(description = "用户名")
  private String username;

  @Schema(description = "密码")
  private String password;

  @Schema(description = "同步间隔（秒）")
  private Integer syncInterval;

  @Schema(description = "是否启用SSL")
  private Boolean enableSsl;

  @Schema(description = "连接超时时间（毫秒）")
  private Integer connectTimeout;

  @Schema(description = "读取超时时间（毫秒）")
  private Integer readTimeout;

}
