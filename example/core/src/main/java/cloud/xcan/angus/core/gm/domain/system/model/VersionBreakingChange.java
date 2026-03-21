package cloud.xcan.angus.core.gm.domain.system.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 版本破坏性变更项
 */
@Data
@Schema(description = "版本破坏性变更项")
public class VersionBreakingChange {

  @Schema(description = "模块", example = "API接口")
  private String module;

  @Schema(description = "描述", example = "移除了废弃的API接口 /api/v1/old/endpoint")
  private String description;

  @Schema(description = "迁移指南", example = "请使用新的接口 /api/v1/new/endpoint 替代")
  private String migrationGuide;

  @Schema(description = "影响范围", example = "所有使用该接口的客户端")
  private String impact;
}
