package cloud.xcan.angus.core.repo.interfaces.access.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "用户权限信息")
public class UserPermissionVo {

  @Schema(description = "用户ID")
  private Long userId;

  @Schema(description = "权限列表")
  private List<String> permissions;
}
