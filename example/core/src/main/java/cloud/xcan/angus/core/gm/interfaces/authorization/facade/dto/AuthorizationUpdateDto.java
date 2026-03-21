package cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "更新授权请求参数")
public class AuthorizationUpdateDto {

  @NotEmpty
  @Schema(description = "角色ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
  private List<Long> roleIds;

  @Schema(description = "授权开始有效性时间")
  private LocalDateTime validFrom;

  @Schema(description = "授权有效性结束时间")
  private LocalDateTime validTo;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "授权描述")
  private String description;

}
