package cloud.xcan.angus.core.gm.interfaces.backup.facade.dto;

import cloud.xcan.angus.core.gm.domain.backup.enums.BackupStatus;
import cloud.xcan.angus.core.gm.domain.backup.enums.BackupType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询备份记录请求参数")
public class BackupFindDto extends PageQuery {

  @Schema(description = "备份名称")
  private String name;

  @Schema(description = "备份类型")
  private BackupType type;

  @Schema(description = "备份状态")
  private BackupStatus status;

  @Schema(description = "开始日期（格式：yyyy-MM-dd HH:mm:ss）")
  private LocalDateTime startDate;

  @Schema(description = "结束日期（格式：yyyy-MM-dd HH:mm:ss）")
  private LocalDateTime endDate;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
