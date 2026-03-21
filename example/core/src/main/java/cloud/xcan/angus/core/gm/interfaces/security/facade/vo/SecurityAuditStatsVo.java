package cloud.xcan.angus.core.gm.interfaces.security.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "安全审计统计VO")
public class SecurityAuditStatsVo {

  @Schema(description = "统计周期")
  private Period period;

  @Schema(description = "总事件数", example = "1250")
  private Long totalEvents;

  @Schema(description = "高风险事件数", example = "25")
  private Long highRiskEvents;

  @Schema(description = "中风险事件数", example = "150")
  private Long mediumRiskEvents;

  @Schema(description = "低风险事件数", example = "1075")
  private Long lowRiskEvents;

  @Schema(description = "登录失败次数", example = "50")
  private Long loginFailures;

  @Schema(description = "密码修改次数", example = "30")
  private Long passwordChanges;

  @Schema(description = "权限变更次数", example = "15")
  private Long permissionChanges;

  @Schema(description = "按日期统计的事件列表")
  private List<DayCount> eventsByDay;

  @Data
  @Schema(description = "统计周期")
  public static class Period {

    @Schema(description = "开始日期", example = "2025-12-01")
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2025-12-31")
    private LocalDate endDate;
  }

  @Data
  @Schema(description = "日期统计")
  public static class DayCount {

    @Schema(description = "日期", example = "2025-12-19")
    private LocalDate date;

    @Schema(description = "事件数量", example = "45")
    private Long count;
  }
}
