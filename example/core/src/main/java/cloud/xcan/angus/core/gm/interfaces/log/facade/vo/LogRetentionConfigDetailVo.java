package cloud.xcan.angus.core.gm.interfaces.log.facade.vo;

import cloud.xcan.angus.remote.NameJoinField;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "日志清理配置详情")
public class LogRetentionConfigDetailVo {

  @Schema(description = "应用ID")
  private Long applicationId;

  @Schema(description = "应用名称")
  @NameJoinField(id = "applicationId", repository = "applicationRepo")
  private String applicationName;

  @Schema(description = "用户日志保留天数")
  private Integer userLogRetentionDays;

  @Schema(description = "系统日志保留天数")
  private Integer systemLogRetentionDays;

  @Schema(description = "API日志保留天数")
  private Integer apiLogRetentionDays;

  @Schema(description = "是否自动清理")
  private Boolean autoCleanup;

  @Schema(description = "清理执行时间")
  private LocalTime cleanupTime;

  @Schema(description = "是否启用")
  private Boolean enabled;

  @Schema(description = "上次清理时间")
  private LocalDateTime lastCleanupDate;

  @Schema(description = "上次清理结果")
  private Map<String, Object> lastCleanupResult;

  @Schema(description = "下次清理时间")
  private LocalDateTime nextCleanupDate;
}
