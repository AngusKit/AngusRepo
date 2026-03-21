package cloud.xcan.angus.api.gm.email.dto;

import static cloud.xcan.angus.api.commonlink.GMConstant.DEFAULT_EMAIL_LANGUAGE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ATTACHMENT_NUM;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X4;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_PARAM_SIZE;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "发送邮件DTO")
public class EmailSendDto {

  @NotBlank
  @Size(max = MAX_NAME_LENGTH)
  @Schema(description = "收件人邮箱", requiredMode = RequiredMode.REQUIRED, example = "user@example.com")
  private String to;

  @Size(max = MAX_NAME_LENGTH)
  @Schema(description = "抄送邮箱")
  private String cc;

  @Size(max = MAX_NAME_LENGTH)
  @Schema(description = "密送邮箱")
  private String bcc;

  @NotBlank
  @Schema(description = "模板编码", requiredMode = RequiredMode.REQUIRED)
  private String templateCode;

  @Schema(description = "模版语言，默认中文：zh_CN", defaultValue = "zh_CN")
  private String language = DEFAULT_EMAIL_LANGUAGE;

  @Size(max = MAX_PARAM_SIZE)
  @Schema(description = "模板参数")
  private Map<String, String> params;

  @Size(max = MAX_ATTACHMENT_NUM)
  @Schema(description = "附件列表")
  private List<EmailAttachment> attachments;

  @Data
  public static class EmailAttachment {

    @Size(max = MAX_NAME_LENGTH_X2)
    @Schema(description = "文件名")
    private String fileName;

    @Size(max = MAX_NAME_LENGTH_X4)
    @Schema(description = "文件URL")
    private String fileUrl;
  }
}
