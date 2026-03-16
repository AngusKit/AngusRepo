package cloud.xcan.angus.core.repo.interfaces.access.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "权限检查结果")
public class PermissionCheckResultVo implements Serializable {

  @Schema(description = "是否允许")
  private Boolean allowed;

  @Schema(description = "原因说明")
  private String reason;
}
