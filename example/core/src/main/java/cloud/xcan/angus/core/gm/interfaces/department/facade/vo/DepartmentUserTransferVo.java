package cloud.xcan.angus.core.gm.interfaces.department.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "转移部门用户响应")
public class DepartmentUserTransferVo {

  @Schema(description = "源部门ID")
  private Long sourceDepartmentId;

  @Schema(description = "目标部门ID")
  private Long targetDepartmentId;

  @Schema(description = "转移成功数量")
  private Integer transferredCount;
}
