package cloud.xcan.angus.core.repo.interfaces.artifact.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "批量删除制品请求参数")
public class ArtifactBatchDeleteDto {

  @NotEmpty
  @Schema(description = "制品ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<Long> ids;
}
