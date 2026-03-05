package cloud.xcan.angus.core.repo.interfaces.user.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "头像上传结果")
public class AvatarUploadResultVo {

  @Schema(description = "头像URL")
  private String avatarUrl;

  @Schema(description = "是否成功")
  private Boolean success;
}
