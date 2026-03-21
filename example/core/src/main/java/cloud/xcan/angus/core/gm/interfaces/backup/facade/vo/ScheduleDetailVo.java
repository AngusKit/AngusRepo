package cloud.xcan.angus.core.gm.interfaces.backup.facade.vo;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.domain.backup.enums.BackupType;
import cloud.xcan.angus.core.gm.domain.backup.enums.ScheduleFrequency;
import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.remote.vo.AuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "备份计划详情")
public class ScheduleDetailVo extends AuditingVo {

  @Schema(description = "计划ID")
  private Long id;

  @Schema(description = "计划名称")
  private String name;

  @Schema(description = "备份类型")
  private BackupType type;

  @Schema(description = "执行频率")
  private ScheduleFrequency frequency;

  @Schema(description = "备份应用ID，不指定时备份所有应用")
  private Long applicationId;

  @Schema(description = "备份应用名称")
  @NameJoinField(id = "applicationId", repository = "applicationRepo")
  private String applicationName;

  @Schema(description = "执行时间（如：02:00）")
  private String time;

  @Schema(description = "保留策略（如：30天、7天）")
  private String retention;

  @Schema(description = "计划状态")
  private EnabledStatus status;

  @Schema(description = "上次运行时间")
  private LocalDateTime lastRun;

  @Schema(description = "下次运行时间")
  private LocalDateTime nextRun;

  @Schema(description = "创建时间")
  private LocalDateTime createdAt;

  @Schema(description = "是否备份日志")
  private Boolean backupLogs;
}
