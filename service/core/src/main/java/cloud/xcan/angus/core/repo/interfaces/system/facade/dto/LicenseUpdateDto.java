package cloud.xcan.angus.core.repo.interfaces.system.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "许可证更新请求参数")
public class LicenseUpdateDto {

  @NotBlank
  @Schema(description = "许可证密钥", requiredMode = Schema.RequiredMode.REQUIRED)
  private String licenseKey;
}
