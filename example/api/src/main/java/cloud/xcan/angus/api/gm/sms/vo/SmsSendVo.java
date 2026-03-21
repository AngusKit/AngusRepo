package cloud.xcan.angus.api.gm.sms.vo;

import cloud.xcan.angus.api.commonlink.sms.SmsStatus;
import cloud.xcan.angus.remote.vo.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "短信发送结果VO")
public class SmsSendVo extends TenantAuditingVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "手机号（脱敏）")
  private String phone;

  @Schema(description = "短信内容")
  private String content;

  @Schema(description = "模板ID")
  private Long templateId;

  @Schema(description = "发送状态")
  private SmsStatus status;

  @Schema(description = "发送时间")
  private LocalDateTime sentTime;

  @Schema(description = "消息ID")
  private String messageId;
}
