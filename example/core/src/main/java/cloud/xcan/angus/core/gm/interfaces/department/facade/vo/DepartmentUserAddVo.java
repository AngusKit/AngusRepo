package cloud.xcan.angus.core.gm.interfaces.department.facade.vo;

import cloud.xcan.angus.api.commonlink.user.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "添加部门用户响应")
public class DepartmentUserAddVo {

  @Schema(description = "部门ID")
  private Long departmentId;

  @Schema(description = "添加成功数量")
  private Integer addedCount;

  @Schema(description = "添加的用户列表")
  private List<UserInfo> addedUsers;

}
