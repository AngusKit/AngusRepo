package cloud.xcan.angus.core.gm.domain.system.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 版本特性项
 */
@Data
@Schema(description = "版本特性项")
public class VersionFeature {

  @Schema(description = "类型", example = "feature")
  private String type;

  @Schema(description = "模块", example = "用户管理")
  private String module;

  @Schema(description = "描述", example = "新增批量导入用户功能")
  private String description;

  @Schema(description = "关联问题ID", example = "ISSUE-1234")
  private String issueId;
}
