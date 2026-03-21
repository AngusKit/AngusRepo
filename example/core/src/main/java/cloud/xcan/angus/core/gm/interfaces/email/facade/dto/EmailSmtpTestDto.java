package cloud.xcan.angus.core.gm.interfaces.email.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_KEY_LENGTH_X2;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "测试SMTP连接DTO")
public class EmailSmtpTestDto {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "SMTP服务器地址", requiredMode = RequiredMode.REQUIRED, example = "smtp.exmail.qq.com")
  private String host;

  @NotNull
  @Schema(description = "SMTP端口", requiredMode = RequiredMode.REQUIRED, example = "465")
  private Integer port;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "用户名", requiredMode = RequiredMode.REQUIRED, example = "notify@angusgm.com")
  private String username;

  @NotBlank
  @Length(max = MAX_KEY_LENGTH_X2)
  @Schema(description = "密码", requiredMode = RequiredMode.REQUIRED)
  private String password;

  @Schema(description = "是否使用SSL（SSL和STARTTLS互斥，只能选择其一）", example = "true")
  private Boolean useSsl = true;

  @Schema(description = "是否使用STARTTLS（SSL和STARTTLS互斥，只能选择其一）", example = "false")
  private Boolean useStartTls = false;

}
