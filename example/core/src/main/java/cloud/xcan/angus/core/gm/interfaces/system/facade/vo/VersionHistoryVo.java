package cloud.xcan.angus.core.gm.interfaces.system.facade.vo;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.gm.domain.system.enums.VersionType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "版本历史")
public class VersionHistoryVo {

  @Schema(description = "版本ID", example = "VER_001")
  private Long id;

  @Schema(description = "版本号", example = "1.5.2")
  private String version;

  @Schema(description = "构建号", example = "20251219001")
  private String buildNumber;

  @Schema(description = "发布日期", example = "2025-12-15 12:00:00")
  private LocalDateTime releaseDate;

  @Schema(description = "发布类型", example = "patch")
  private VersionType releaseType;

  @Schema(description = "标题", example = "Bug修复和性能优化")
  private String title;

  @Schema(description = "描述", example = "修复了多个已知问题，优化了系统性能")
  private String description;

  @Schema(description = "部署时间")
  private LocalDateTime deploymentDate;

  @Schema(description = "部署人", example = "系统管理员")
  private String deployedBy;

  @Schema(description = "应用编码", example = "angus-gm")
  private String appCode;

  @Schema(description = "版本类型", example = "ENTERPRISE")
  private EditionType editionType;
}
