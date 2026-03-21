package cloud.xcan.angus.core.gm.interfaces.backup.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_FILE_PATH;

import cloud.xcan.angus.core.gm.domain.backup.enums.RestoreSource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "创建恢复任务请求参数")
public class RestoreCreateDto {

  @NotNull
  @Schema(description = "恢复源类型", requiredMode = Schema.RequiredMode.REQUIRED)
  private RestoreSource source;

  @Schema(description = "备份ID(source=BACKUP时必填)")
  private Long backupId;

  @Length(max = MAX_FILE_PATH)
  @Schema(description = "文件路径(source=FILE_PATH时必填)")
  private String filePath;

  @NotNull
  @Valid
  @Schema(description = "恢复选项", requiredMode = Schema.RequiredMode.REQUIRED)
  private RestoreOptionsDto options;

  @NotBlank
  @Schema(description = "管理员密码验证", requiredMode = Schema.RequiredMode.REQUIRED)
  private String password;
}
