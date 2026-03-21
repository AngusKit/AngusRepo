package cloud.xcan.angus.core.gm.interfaces.backup.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.core.gm.domain.backup.enums.BackupStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "更新备份请求参数")
public class BackupUpdateDto {

  @Schema(description = "备份ID（路径参数，无需在请求体中传递）", hidden = true)
  private Long id;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "备份名称")
  private String name;

  @Schema(description = "备份状态", allowableValues = {"PENDING", "IN_PROGRESS", "COMPLETED",
      "FAILED", "CANCELLED", "RESTORING"})
  private BackupStatus status;

  @Schema(description = "保留天数（单位：天）")
  private Integer retentionDays;

  @Schema(description = "是否自动删除")
  private Boolean autoDelete;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "描述")
  private String description;
}
