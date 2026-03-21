package cloud.xcan.angus.core.gm.interfaces.system.facade.dto;

import cloud.xcan.angus.core.gm.domain.system.enums.VersionType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "变更日志查询DTO")
public class ChangelogFindDto extends PageQuery {

  @Schema(description = "应用编码")
  private String appCode;

  @Schema(description = "版本类型")
  private VersionType type;

  @Schema(description = "指定版本号", example = "1.5.2")
  private String version;

  @Override
  public String getDefaultOrderBy() {
    return "releaseDate";
  }
}
