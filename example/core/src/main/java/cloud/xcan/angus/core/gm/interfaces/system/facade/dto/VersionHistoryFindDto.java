package cloud.xcan.angus.core.gm.interfaces.system.facade.dto;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.gm.domain.system.enums.VersionStatus;
import cloud.xcan.angus.core.gm.domain.system.enums.VersionType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "版本历史查询DTO")
public class VersionHistoryFindDto extends PageQuery {

  @Schema(description = "版本类型")
  private VersionType type;

  @Schema(description = "版本状态")
  private VersionStatus status;

  @Schema(description = "应用编码")
  private String appCode;

  @Schema(description = "版本类型")
  private EditionType editionType;

  @Override
  public String getDefaultOrderBy() {
    return "releaseDate";
  }
}
