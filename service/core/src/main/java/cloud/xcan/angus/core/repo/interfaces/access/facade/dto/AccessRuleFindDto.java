package cloud.xcan.angus.core.repo.interfaces.access.facade.dto;

import cloud.xcan.angus.core.repo.domain.access.AccessPrincipalType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "查询访问规则列表请求参数")
public class AccessRuleFindDto extends PageQuery {

  @Schema(description = "仓库ID筛选")
  private Long repositoryId;

  @Schema(description = "主体类型筛选")
  private AccessPrincipalType principalType;

  @Schema(description = "是否启用筛选")
  private Boolean enabled;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
