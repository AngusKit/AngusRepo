package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "短信统计数据VO")
public class SmsStatsVo {

  @Schema(description = "总发送数量")
  private Long totalSent;

  @Schema(description = "成功数量")
  private Long successCount;

  @Schema(description = "失败数量")
  private Long failedCount;

  @Schema(description = "今日发送数量")
  private Long todaySent;

  @Schema(description = "本月发送数量")
  private Long thisMonthSent;

}
