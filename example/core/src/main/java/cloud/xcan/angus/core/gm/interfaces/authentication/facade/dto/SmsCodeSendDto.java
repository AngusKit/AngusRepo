package cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto;

import cloud.xcan.angus.api.commonlink.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
public class SmsCodeSendDto {

  @NotEmpty
  @Schema(description = "短信验证码对应模版编码："
      + "短信验证码-VerificationCode",
      allowableValues = {"VerificationCode"},
      example = "LoginVerification", requiredMode = RequiredMode.REQUIRED)
  private String templateCode;

  @NotEmpty
  @Schema(description = "接收短信验证码手机号", requiredMode = RequiredMode.REQUIRED)
  private String phone;

  @Schema(description = "模版语言，默认中文：zh-CN", defaultValue = "zh-CN")
  private Language language;

}
