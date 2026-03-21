package cloud.xcan.angus.api.gm.email.dto;

import static cloud.xcan.angus.api.commonlink.GMConstant.DEFAULT_EMAIL_LANGUAGE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_PARAM_SIZE;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "批量发送邮件DTO")
public class EmailSendBatchDto {

  @NotEmpty
  @Size(max = MAX_BATCH_SIZE)
  @Schema(description = "收件人邮箱列表", requiredMode = RequiredMode.REQUIRED)
  private List<String> to;

  @NotBlank
  @Schema(description = "模板编码", requiredMode = RequiredMode.REQUIRED)
  private String templateCode;

  @Schema(description = "模版语言，默认中文：zh_CN", defaultValue = "zh_CN")
  private String language = DEFAULT_EMAIL_LANGUAGE;

  @Size(max = MAX_PARAM_SIZE)
  @Schema(description = "模板参数")
  private Map<String, String> params;
}
