package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "更新服务商配置DTO")
public class SmsProviderUpdateDto {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "服务商名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Schema(description = "是否默认，默认false", example = "false")
  private Boolean isDefault;

  @Schema(description = "配置信息", requiredMode = Schema.RequiredMode.REQUIRED)
  private Map<String, String> config;
}
