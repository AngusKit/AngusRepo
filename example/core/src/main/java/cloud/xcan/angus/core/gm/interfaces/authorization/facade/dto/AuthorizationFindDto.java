package cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询授权请求参数")
public class AuthorizationFindDto extends PageQuery {

  @Schema(description = "授权ID")
  private Long id;

  @Schema(description = "授权主体类型")
  private AuthorizationSubjectType subjectType;

  @Schema(description = "授权主体ID")
  private Long subjectId;

  @Schema(description = "启用状态")
  private EnabledStatus status;

  @Schema(description = "应用ID")
  private Long appId;

  @Schema(description = "角色ID")
  private Long roleId;

}
