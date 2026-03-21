package cloud.xcan.angus.core.gm.interfaces.group.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "添加组成员请求参数")
public class GroupUserAddDto {

  @NotEmpty
  @Schema(description = "用户ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<Long> userIds;
}
