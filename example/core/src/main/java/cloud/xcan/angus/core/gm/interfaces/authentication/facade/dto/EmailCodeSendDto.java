package cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto;

import cloud.xcan.angus.api.commonlink.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EmailCodeSendDto {

  @NotEmpty
  @Schema(description = "邮件验证码对应模版编码："
      + "登录邮件验证码-LoginVerification；"
      + "注册邮件验证码-RegisterVerification；"
      + "找回密码邮件验证码-RetrievePassword",
      allowableValues = {"LoginVerification", "RegisterVerification", "RetrievePassword"},
      example = "LoginVerification", requiredMode = RequiredMode.REQUIRED)
  private String templateCode;

  @Schema(description = "模版语言，默认中文：en-US", defaultValue = "en-US")
  private Language language;

  @Email
  @NotEmpty
  @Schema(description = "接收邮件验证码地址", requiredMode = RequiredMode.REQUIRED)
  private String email;
}
