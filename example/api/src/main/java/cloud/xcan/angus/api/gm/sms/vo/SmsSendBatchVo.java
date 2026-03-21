package cloud.xcan.angus.api.gm.sms.vo;

import cloud.xcan.angus.api.commonlink.sms.SmsStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "批量发送短信结果VO")
public class SmsSendBatchVo {

  @Schema(description = "总数量")
  private Integer totalCount;

  @Schema(description = "成功数量")
  private Integer successCount;

  @Schema(description = "失败数量")
  private Integer failedCount;

  @Schema(description = "发送结果列表")
  private List<SmsSendResultVo> results;

  @Data
  @Schema(description = "单条短信发送结果")
  public static class SmsSendResultVo {

    @Schema(description = "手机号（脱敏）")
    private String phone;

    @Schema(description = "发送状态")
    private SmsStatus status;

    @Schema(description = "消息ID")
    private String messageId;
  }
}
