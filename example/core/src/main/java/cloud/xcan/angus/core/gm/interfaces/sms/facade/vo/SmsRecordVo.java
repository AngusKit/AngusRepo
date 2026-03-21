package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo;

import cloud.xcan.angus.api.commonlink.sms.SmsStatus;
import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.remote.vo.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "短信记录VO")
public class SmsRecordVo extends TenantAuditingVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "手机号（脱敏）")
  private String phone;

  @Schema(description = "短信内容")
  private String content;

  @Schema(description = "模板ID")
  private Long templateId;

  @Schema(description = "模板名称")
  @NameJoinField(id = "templateId", repository = "smsTemplateRepo")
  private String templateName;

  @Schema(description = "发送状态")
  private SmsStatus status;

  @Schema(description = "发送时间")
  private LocalDateTime sentTime;

  @Schema(description = "送达时间")
  private LocalDateTime deliveredTime;

  @Schema(description = "服务商")
  private String provider;

}
