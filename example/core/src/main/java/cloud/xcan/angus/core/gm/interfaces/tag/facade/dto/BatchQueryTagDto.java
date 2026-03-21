package cloud.xcan.angus.core.gm.interfaces.tag.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "批量查询标签DTO")
public class BatchQueryTagDto implements Serializable {

  @NotNull
  @Size(min = 1, max = 100)
  @Schema(description = "标签ID列表", requiredMode = RequiredMode.REQUIRED)
  private List<Long> ids;
}
