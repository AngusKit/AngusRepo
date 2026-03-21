package cloud.xcan.angus.core.gm.interfaces.system.facade.dto;

import cloud.xcan.angus.api.commonlink.setting.alert.AlertLevel;
import cloud.xcan.angus.core.gm.domain.system.enums.AlertRecordStatus;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "告警记录查询DTO")
public class AlertRecordFindDto extends PageQuery {

  @Schema(description = "实例ID")
  private String instanceId;

  @Schema(description = "等级筛选（低、中、高）")
  private AlertLevel level;

  @Schema(description = "状态筛选（待处理、已处理、已忽略）")
  private AlertRecordStatus status;

  @Schema(description = "触发时间")
  private LocalDateTime triggerTime;

  @Override
  public String getDefaultOrderBy() {
    return "triggerTime";
  }
}
