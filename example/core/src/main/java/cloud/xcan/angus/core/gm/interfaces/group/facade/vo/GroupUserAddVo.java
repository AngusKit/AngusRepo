package cloud.xcan.angus.core.gm.interfaces.group.facade.vo;

import cloud.xcan.angus.api.commonlink.user.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "添加组成员响应")
public class GroupUserAddVo {

  @Schema(description = "组ID")
  private Long groupId;

  @Schema(description = "添加数量")
  private Integer addedCount;

  @Schema(description = "已添加用户列表")
  private List<UserInfo> addedUsers;

}
