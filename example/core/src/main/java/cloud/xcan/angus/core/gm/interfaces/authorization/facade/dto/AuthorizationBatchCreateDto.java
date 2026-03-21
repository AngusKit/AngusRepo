package cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;

import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "批量授权请求参数")
public class AuthorizationBatchCreateDto {

  @NotNull
  @Schema(description = "授权主体类型", requiredMode = Schema.RequiredMode.REQUIRED)
  private AuthorizationSubjectType subjectType;

  @NotEmpty
  @Schema(description = "授权主体ID列表")
  private List<Long> subjectIds;

  @NotEmpty
  @Schema(description = "角色ID列表")
  private List<Long> roleIds;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "授权描述")
  private String description;
}
