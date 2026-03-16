package cloud.xcan.angus.core.repo.interfaces.activitylog.facade.vo;

import cloud.xcan.angus.core.repo.domain.activitylog.ActivityAction;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

/**
 * 活动趋势视图对象
 */
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "活动趋势")
public class ActivityTrendVo implements Serializable {

  @Schema(description = "日期")
  private String date;

  @Schema(description = "数量")
  private Long count;

  @Schema(description = "操作类型分解")
  private Map<ActivityAction, Long> actionBreakdown;
}
