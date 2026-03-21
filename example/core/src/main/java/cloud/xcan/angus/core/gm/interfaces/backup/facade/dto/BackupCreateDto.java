package cloud.xcan.angus.core.gm.interfaces.backup.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.core.gm.domain.backup.enums.BackupType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "创建备份请求参数")
public class BackupCreateDto {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "备份名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Schema(description = "备份类型，默认全量备份", defaultValue = "FULL")
  private BackupType type;

  @Schema(description = "备份应用ID，不指定时备份所有应用")
  private Long applicationId;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "描述")
  private String description;

  @Schema(description = "是否备份日志，默认不开启")
  private Boolean backupLogs = false;
}
