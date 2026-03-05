package cloud.xcan.angus.core.repo.interfaces.user.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新个人信息请求参数")
public class UserProfileUpdateDto {

  @Size(max = 255)
  @Schema(description = "用户名称")
  private String name;

  @Schema(description = "头像URL")
  private String avatar;

  @Size(max = 255)
  @Schema(description = "部门")
  private String department;
}
