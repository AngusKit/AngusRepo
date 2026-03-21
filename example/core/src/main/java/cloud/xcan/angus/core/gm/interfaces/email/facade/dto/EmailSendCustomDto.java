package cloud.xcan.angus.core.gm.interfaces.email.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ATTACHMENT_NUM;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH_X100;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X4;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "发送自定义邮件DTO")
public class EmailSendCustomDto {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "收件人邮箱", requiredMode = RequiredMode.REQUIRED, example = "user@example.com")
  private String to;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "抄送邮箱")
  private String cc;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "密送邮箱")
  private String bcc;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH_X4)
  @Schema(description = "邮件主题", requiredMode = RequiredMode.REQUIRED, example = "重要通知")
  private String subject;

  @NotBlank
  @Length(max = MAX_DESC_LENGTH_X100)
  @Schema(description = "邮件内容", requiredMode = RequiredMode.REQUIRED)
  private String content;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "内容类型", example = "html")
  private String contentType = "html";

  @Length(max = MAX_ATTACHMENT_NUM)
  @Schema(description = "附件列表")
  private List<EmailAttachment> attachments;

  @Data
  @Schema(description = "邮件附件")
  public static class EmailAttachment {

    @Length(max = MAX_NAME_LENGTH_X2)
    @Schema(description = "文件名")
    private String fileName;

    @Length(max = MAX_NAME_LENGTH_X4)
    @Schema(description = "文件URL")
    private String fileUrl;
  }
}
