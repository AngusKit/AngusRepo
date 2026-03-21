package cloud.xcan.angus.core.gm.domain.system.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 版本Bug修复项
 */
@Data
@Schema(description = "版本Bug修复项")
public class VersionBugFix {

  @Schema(description = "类型", example = "fix")
  private String type;

  @Schema(description = "模块", example = "用户管理")
  private String module;

  @Schema(description = "描述", example = "修复了批量导入用户时的数据校验问题")
  private String description;

  @Schema(description = "关联问题ID", example = "ISSUE-1234")
  private String issueId;
}
