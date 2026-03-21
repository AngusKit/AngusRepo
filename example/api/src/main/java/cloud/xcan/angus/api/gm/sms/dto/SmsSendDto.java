package cloud.xcan.angus.api.gm.sms.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_MOBILE_LENGTH;

import cloud.xcan.angus.api.commonlink.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "发送短信DTO")
public class SmsSendDto {

  @NotBlank
  @Size(max = MAX_MOBILE_LENGTH)
  @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
  private String phone;

  @NotEmpty
  @Schema(description = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String templateCode;

  @Schema(description = "语言，不指定则默认中文", example = "zh-CN")
  private Language language;

  @Schema(description = "模板参数")
  private Map<String, String> params;
}
