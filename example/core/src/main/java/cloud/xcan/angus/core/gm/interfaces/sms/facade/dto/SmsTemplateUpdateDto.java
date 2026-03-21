package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH_X10;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.commonlink.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "更新短信模板DTO")
public class SmsTemplateUpdateDto {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String code;

  @NotNull
  @Schema(description = "语言", requiredMode = Schema.RequiredMode.REQUIRED, example = "zh-CN")
  private Language language;

  @NotBlank
  @Length(max = MAX_DESC_LENGTH_X10)
  @Schema(description = "模板内容", requiredMode = Schema.RequiredMode.REQUIRED)
  private String content;

  @Schema(description = "模板参数")
  private List<String> params;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "服务商")
  private String provider;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "服务商模板编码")
  private String templateCode;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "签名")
  private String signature;
}
