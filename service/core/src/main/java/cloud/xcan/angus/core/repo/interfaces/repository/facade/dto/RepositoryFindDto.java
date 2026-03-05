package cloud.xcan.angus.core.repo.interfaces.repository.facade.dto;

import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryStatus;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "查询仓库列表请求参数")
public class RepositoryFindDto extends PageQuery {

  @Schema(description = "仓库格式筛选")
  private RepositoryFormat format;

  @Schema(description = "仓库类型筛选")
  private RepositoryType type;

  @Schema(description = "仓库状态筛选")
  private RepositoryStatus status;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
