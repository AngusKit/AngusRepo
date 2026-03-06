package cloud.xcan.angus.core.repo.interfaces.format.facade.dto;

import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "查询格式元数据列表请求参数")
public class FormatMetadataFindDto extends PageQuery {

  @NotNull
  @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long repositoryId;

  @NotNull
  @Schema(description = "仓库格式", requiredMode = Schema.RequiredMode.REQUIRED)
  private RepositoryFormat format;

  @Schema(description = "制品名称筛选")
  private String name;

  @Schema(description = "版本筛选")
  private String version;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
