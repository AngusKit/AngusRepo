package cloud.xcan.angus.api.gm.email.vo;

import cloud.xcan.angus.api.commonlink.email.EmailStatus;
import cloud.xcan.angus.remote.vo.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "发送邮件响应VO")
public class EmailSendVo extends TenantAuditingVo {

  @Schema(description = "邮件记录ID")
  private Long id;

  @Schema(description = "收件人邮箱")
  private String to;

  @Schema(description = "邮件主题")
  private String subject;

  @Schema(description = "模板ID")
  private Long templateId;

  @Schema(description = "发送状态")
  private EmailStatus status;

  @Schema(description = "发送时间")
  private LocalDateTime sentTime;

  @Schema(description = "消息ID")
  private String messageId;
}
