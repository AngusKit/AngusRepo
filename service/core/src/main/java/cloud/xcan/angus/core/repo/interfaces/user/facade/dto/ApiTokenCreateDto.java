package cloud.xcan.angus.core.repo.interfaces.user.facade.dto;

import cloud.xcan.angus.core.repo.domain.user.TokenPermission;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;
import java.io.Serializable;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "创建API Token请求参数")
public class ApiTokenCreateDto implements Serializable {

  @NotBlank
  @Size(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "Token名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Schema(description = "Token描述")
  private String description;

  @NotNull
  @Schema(description = "Token权限", requiredMode = Schema.RequiredMode.REQUIRED)
  private TokenPermission permission;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "过期时间")
  private LocalDateTime expiresAt;
}
