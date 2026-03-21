package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH_X10;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_MOBILE_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "测试短信DTO")
public class SmsTestDto {

  @NotBlank
  @Length(max = MAX_MOBILE_LENGTH)
  @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
  private String phone;

  @NotBlank
  @Length(max = MAX_DESC_LENGTH_X10)
  @Schema(description = "短信内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "这是一条测试短信")
  private String content;
}
