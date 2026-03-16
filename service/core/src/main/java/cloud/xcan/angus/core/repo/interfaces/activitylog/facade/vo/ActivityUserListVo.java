package cloud.xcan.angus.core.repo.interfaces.activitylog.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

/**
 * 活动用户列表视图对象
 */
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "活动用户列表")
public class ActivityUserListVo implements Serializable {

  @Schema(description = "用户列表")
  private List<String> users;
}
