package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo;

import cloud.xcan.angus.api.commonlink.sms.SmsStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "测试短信发送结果VO")
public class SmsTestVo {

  @Schema(description = "手机号（脱敏）")
  private String phone;

  @Schema(description = "发送状态")
  private SmsStatus status;

  @Schema(description = "消息ID")
  private String messageId;

  @Schema(description = "发送时间")
  private LocalDateTime sentTime;
}
