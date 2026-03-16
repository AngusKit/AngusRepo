package cloud.xcan.angus.core.repo.interfaces.user.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新个人信息请求参数")
public class UserProfileUpdateDto implements Serializable {

  @Size(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "用户名称")
  private String name;

  @Schema(description = "头像URL")
  private String avatar;

  @Size(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "部门")
  private String department;
}
