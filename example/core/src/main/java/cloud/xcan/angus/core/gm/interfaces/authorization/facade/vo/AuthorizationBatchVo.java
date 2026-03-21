package cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo;

import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "批量授权结果")
public class AuthorizationBatchVo {

  @Schema(description = "授权主体类型")
  private AuthorizationSubjectType subjectType;

  @Schema(description = "授权主体数量")
  private int subjectCount;

  @Schema(description = "角色数量")
  private int roleCount;

  @Schema(description = "成功数量")
  private int successCount;

}
