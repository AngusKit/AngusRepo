package cloud.xcan.angus.core.gm.interfaces.email.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH_X100;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

import cloud.xcan.angus.api.commonlink.Language;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "更新邮件模板DTO")
public class EmailTemplateUpdateDto {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "模板名称", requiredMode = RequiredMode.REQUIRED, example = "系统通知邮件")
  private String name;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "模板编码", requiredMode = RequiredMode.REQUIRED, example = "SYSTEM_NOTIFY")
  private String code;

  @NotNull
  @Schema(description = "语言", requiredMode = RequiredMode.REQUIRED, example = "zh-CN")
  private Language language;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "邮件主题", requiredMode = RequiredMode.REQUIRED, example = "【AngusGM】{event}通知")
  private String subject;

  @NotBlank
  @Length(max = MAX_DESC_LENGTH_X100)
  @Schema(description = "模板内容", requiredMode = RequiredMode.REQUIRED)
  private String content;

  @Size(max = MAX_BATCH_SIZE)
  @Schema(description = "模板参数")
  private List<String> params;
}
