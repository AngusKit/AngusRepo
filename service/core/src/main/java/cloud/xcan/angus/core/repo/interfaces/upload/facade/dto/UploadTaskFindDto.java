package cloud.xcan.angus.core.repo.interfaces.upload.facade.dto;

import cloud.xcan.angus.core.repo.domain.upload.UploadStatus;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "查询上传任务列表请求参数")
public class UploadTaskFindDto extends PageQuery {

  @Schema(description = "仓库ID筛选")
  private Long repositoryId;

  @Schema(description = "上传状态筛选")
  private UploadStatus status;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
