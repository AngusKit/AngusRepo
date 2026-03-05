package cloud.xcan.angus.core.repo.interfaces.security.facade.dto;

import cloud.xcan.angus.core.repo.domain.security.ScanStatus;
import cloud.xcan.angus.core.repo.domain.security.ScanType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "查询扫描任务列表请求参数")
public class ScanTaskFindDto extends PageQuery {

  @Schema(description = "仓库ID筛选")
  private String repositoryId;

  @Schema(description = "制品ID筛选")
  private String artifactId;

  @Schema(description = "扫描类型筛选")
  private ScanType scanType;

  @Schema(description = "状态筛选")
  private ScanStatus status;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
