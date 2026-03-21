package cloud.xcan.angus.core.gm.interfaces.application.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.application.enums.ApplicationMenuType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新应用菜单请求参数")
public class ApplicationMenuUpdateDto implements Serializable {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Length(max = 40)
  @Schema(description = "页面实际展示的简化名称，不设置时默认成名称")
  private String showName;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "菜单编码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String code;

  @Length(max = MAX_URL_LENGTH)
  @Schema(description = "菜单图标")
  private String icon;

  @Length(max = MAX_URL_LENGTH)
  @Schema(description = "菜单路径")
  private String path;

  @Schema(description = "父菜单ID")
  private Long parentId;

  @Schema(description = "排序顺序，值越小越靠前")
  private Integer sortOrder;

  @Schema(description = "有效状态，默认启用")
  private EnabledStatus status;

  @Schema(description = "菜单类型", requiredMode = RequiredMode.REQUIRED)
  private ApplicationMenuType type;

  @Schema(description = "是否授权控制，默认开启即授权后访问")
  private Boolean requiresAuth;

  @Schema(description = "权限信息")
  private PermissionDto permission;
}
