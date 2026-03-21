package cloud.xcan.angus.core.gm.interfaces.service.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CLIENT_SECRET_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "Eureka连接测试DTO")
public class EurekaTestDto implements Serializable {

  @Length(max = MAX_URL_LENGTH)
  @Schema(description = "Eureka服务URL", example = "http://eureka-server:8761/eureka",
      requiredMode = Schema.RequiredMode.REQUIRED)
  private String serviceUrl;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "用户名", example = "admin")
  private String username;

  @Length(max = MAX_CLIENT_SECRET_LENGTH)
  @Schema(description = "密码", example = "password123")
  private String password;

  @Schema(description = "是否启用SSL", example = "false")
  private Boolean enableSsl;

  @Min(1000)
  @Max(60000)
  @Schema(description = "连接超时时间（毫秒）", example = "5000")
  private Integer connectTimeout;

  @Min(1000)
  @Max(60000)
  @Schema(description = "读取超时时间（毫秒）", example = "10000")
  private Integer readTimeout;
}
