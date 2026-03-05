package cloud.xcan.angus.core.repo.interfaces.upload.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "批量创建上传任务请求参数")
public class BatchUploadCreateDto {

  @NotNull
  @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long repositoryId;

  @Valid
  @NotEmpty
  @Schema(description = "上传文件列表", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<UploadFileInfoDto> files;
}
