package cloud.xcan.angus.api.gm.sms.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.api.commonlink.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "批量发送短信DTO")
public class SmsSendBatchDto {

  @NotEmpty
  @Size(max = MAX_BATCH_SIZE)
  @Schema(description = "手机号列表", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<String> phones;

  @NotEmpty
  @Schema(description = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String templateCode;

  @Schema(description = "语言，不指定则默认中文", example = "zh-CN")
  private Language language;

  @Schema(description = "模板参数")
  private Map<String, String> params;
}
