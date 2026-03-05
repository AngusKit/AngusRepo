package cloud.xcan.angus.core.repo.interfaces.artifact.facade.dto;

import cloud.xcan.angus.core.repo.domain.artifact.ArtifactFormat;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "查询制品列表请求参数")
public class ArtifactFindDto extends PageQuery {

  @Schema(description = "仓库ID筛选")
  private Long repositoryId;

  @Schema(description = "制品名称筛选")
  private String name;

  @Schema(description = "制品格式筛选")
  private ArtifactFormat format;

  @Schema(description = "搜索关键词")
  private String search;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
