package cloud.xcan.angus.core.gm.interfaces.role.facade.vo;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.application.enums.ApplicationMenuType;
import cloud.xcan.angus.api.commonlink.role.PermissionInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "可授权应用菜单（包含授权状态）")
public class AuthorizableApplicationMenuVo {

  @Schema(description = "菜单ID")
  private Long id;

  @Schema(description = "应用ID")
  private Long applicationId;

  @Schema(description = "菜单名称")
  private String name;

  @Schema(description = "页面实际展示的简化名称，不设置时默认成名称")
  private String showName;

  @Schema(description = "菜单编码")
  private String code;

  @Schema(description = "菜单图标")
  private String icon;

  @Schema(description = "菜单路径")
  private String path;

  @Schema(description = "父菜单ID")
  private Long parentId;

  @Schema(description = "排序顺序")
  private Integer sortOrder;

  @Schema(description = "有效状态，默认启用")
  private EnabledStatus status;

  @Schema(description = "菜单类型")
  private ApplicationMenuType type;

  @Schema(description = "是否授权控制，默认开启即授权后访问")
  private Boolean requiresAuth;

  @Schema(description = "权限信息")
  private PermissionInfo permission;

  @Schema(description = "是否已授权给角色")
  private Boolean authorized;

  @Schema(description = "子菜单列表")
  private List<AuthorizableApplicationMenuVo> children;

}
