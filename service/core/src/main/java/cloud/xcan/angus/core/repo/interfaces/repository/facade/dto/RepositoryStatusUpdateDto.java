package cloud.xcan.angus.core.repo.interfaces.repository.facade.dto;

import cloud.xcan.angus.core.repo.domain.repository.RepositoryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新仓库状态请求参数")
public class RepositoryStatusUpdateDto implements Serializable {

  @NotNull
  @Schema(description = "仓库状态", requiredMode = Schema.RequiredMode.REQUIRED)
  private RepositoryStatus status;
}
