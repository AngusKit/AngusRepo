package cloud.xcan.angus.api.commonlink.setting.eureka;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "Eureka配置")
public class EurekaConfig implements Serializable {

  @Schema(description = "Eureka服务URL", example = "http://localhost:1806/eureka")
  private String serviceUrl;

  @Schema(description = "是否启用认证", example = "true")
  private Boolean enableAuth;

  @Schema(description = "用户名", example = "admin")
  private String username;

  @Schema(description = "密码", example = "password123")
  private String password;

  @Schema(description = "同步间隔（秒）", example = "30")
  private Integer syncInterval;

  @Schema(description = "是否启用SSL", example = "false")
  private Boolean enableSsl;

  @Schema(description = "连接超时时间（毫秒）", example = "5000")
  private Integer connectTimeout;

  @Schema(description = "读取超时时间（毫秒）", example = "10000")
  private Integer readTimeout;

}
