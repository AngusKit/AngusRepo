package cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "授权统计数据")
public class AuthorizationStatsVo {

  @Schema(description = "总授权数")
  private Long totalAuthorizations;

  @Schema(description = "用户授权数")
  private Long userAuthorizations;

  @Schema(description = "部门授权数")
  private Long departmentAuthorizations;

  @Schema(description = "组授权数")
  private Long groupAuthorizations;
}
