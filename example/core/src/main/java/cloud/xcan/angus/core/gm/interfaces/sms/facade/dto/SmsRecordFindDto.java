package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto;

import cloud.xcan.angus.api.commonlink.sms.SmsStatus;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "短信记录查询DTO")
public class SmsRecordFindDto extends PageQuery {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "手机号")
  private String phone;

  @Schema(description = "模板ID")
  private Long templateId;

  @Schema(description = "发送状态")
  private SmsStatus status;

  @Schema(description = "发送时间")
  private LocalDateTime sentTime;

  @Override
  public String getDefaultOrderBy() {
    return "sendTime";
  }
}
