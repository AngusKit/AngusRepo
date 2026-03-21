package cloud.xcan.angus.api.gm.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Data
@Accessors(chain = true)
@Schema(description = "锁定/解锁用户请求参数")
public class UserLockDto {

  @NotNull
  @Schema(description = "是否锁定", requiredMode = RequiredMode.REQUIRED)
  private Boolean locked;

  @Length(max = 500)
  @Schema(description = "锁定原因")
  private String reason;
}
