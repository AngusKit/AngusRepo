package cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto;

import cloud.xcan.angus.core.repo.domain.cleanup.CleanupType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "查询清理策略列表请求参数")
public class CleanupPolicyFindDto extends PageQuery {

  @Schema(description = "仓库ID筛选")
  private String repositoryId;

  @Schema(description = "清理类型筛选")
  private CleanupType type;

  @Schema(description = "是否启用筛选")
  private Boolean enabled;

  @Schema(description = "搜索关键词")
  private String search;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
