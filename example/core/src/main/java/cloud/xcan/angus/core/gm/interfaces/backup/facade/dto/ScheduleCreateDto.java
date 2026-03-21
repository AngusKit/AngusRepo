package cloud.xcan.angus.core.gm.interfaces.backup.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.domain.backup.enums.BackupType;
import cloud.xcan.angus.core.gm.domain.backup.enums.ScheduleFrequency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "创建备份计划请求参数")
public class ScheduleCreateDto {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "计划名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @NotNull
  @Schema(description = "备份类型", requiredMode = Schema.RequiredMode.REQUIRED)
  private BackupType type;

  @NotNull
  @Schema(description = "执行频率", requiredMode = Schema.RequiredMode.REQUIRED)
  private ScheduleFrequency frequency;

  @Schema(description = "备份应用ID，不指定时备份所有应用")
  private Long applicationId;

  @Length(max = 20)
  @Schema(description = "执行时间（如：02:00）")
  private String time;

  @Length(max = 50)
  @Schema(description = "保留策略（如：30天、7天）")
  private String retention;

  @Schema(description = "计划状态，默认启用")
  private EnabledStatus status;

  @Schema(description = "是否备份日志，默认不开启")
  private Boolean backupLogs = false;
}
