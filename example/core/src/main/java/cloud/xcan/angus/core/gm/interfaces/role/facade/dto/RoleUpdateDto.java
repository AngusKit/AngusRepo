package cloud.xcan.angus.core.gm.interfaces.role.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH_X4;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_PARAM_SIZE;

import cloud.xcan.angus.api.commonlink.role.enums.RoleEffect;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "更新角色请求参数")
public class RoleUpdateDto {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "角色编码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String code;

  @Length(max = MAX_DESC_LENGTH_X4)
  @Schema(description = "描述")
  private String description;

  //  @Size(max = MAX_OUT_ID_LENGTH)
  //  @Schema(description = "应用ID")
  //  private Long appId;

  @Schema(description = "是否设为默认角色，默认false。注意：应用默认角色针对所有用户自动生效")
  private Boolean isDefault;

  @Schema(description = "角色效果，默认允许，ALLOW：允许，DENY：拒绝")
  private RoleEffect effect;

  @Valid
  @Size(max = MAX_PARAM_SIZE)
  @Schema(description = "权限列表")
  private List<RolePermissionDto> permissions;

}
