package cloud.xcan.angus.core.repo.interfaces.user.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "密码修改结果")
public class PasswordChangeResultVo {

  @Schema(description = "是否成功")
  private Boolean success;

  @Schema(description = "消息")
  private String message;
}
