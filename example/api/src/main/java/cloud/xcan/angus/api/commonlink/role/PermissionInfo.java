package cloud.xcan.angus.api.commonlink.role;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
@Schema(description = "权限信息")
public class PermissionInfo {

  @Schema(description = "父菜单ID，授权应用功能时必须")
  private Long parentMenuId;

  @Schema(description = "菜单ID，授权应用功能时必须")
  private Long menuId;

  @Schema(description = "菜单名称", accessMode = Schema.AccessMode.READ_ONLY)
  private String menuName;

  @Schema(description = "资源标识")
  private String resource;

  @Schema(description = "资源名称", accessMode = Schema.AccessMode.READ_ONLY)
  private String resourceName;

  @Schema(description = "操作列表")
  private List<String> actions;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PermissionInfo that)) {
      return false;
    }
    return Objects.equals(parentMenuId, that.parentMenuId)
        && Objects.equals(menuId, that.menuId)
        && Objects.equals(menuName, that.menuName)
        && Objects.equals(resource, that.resource)
        && Objects.equals(resourceName, that.resourceName)
        && Objects.equals(actions, that.actions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parentMenuId, menuId, menuName, resource, resourceName, actions);
  }
}
