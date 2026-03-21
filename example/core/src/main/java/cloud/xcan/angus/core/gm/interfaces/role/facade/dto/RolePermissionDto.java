package cloud.xcan.angus.core.gm.interfaces.role.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_PARAM_SIZE;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "权限项")
public class RolePermissionDto {

  @Schema(description = "菜单ID，授权应用功能时必须")
  private Long menuId;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "资源标识", requiredMode = Schema.RequiredMode.REQUIRED)
  private String resource;

  @NotNull
  @Size(min = 1, max = MAX_PARAM_SIZE)
  @Schema(description = "操作列表", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<@NotBlank @Size(max = MAX_CODE_LENGTH) String> actions;
}
