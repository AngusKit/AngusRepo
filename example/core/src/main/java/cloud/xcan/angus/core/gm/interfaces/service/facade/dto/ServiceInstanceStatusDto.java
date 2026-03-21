package cloud.xcan.angus.core.gm.interfaces.service.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "服务实例状态DTO")
public class ServiceInstanceStatusDto implements Serializable {

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "实例状态", example = "UP", requiredMode = Schema.RequiredMode.REQUIRED,
      allowableValues = {"UP", "DOWN", "OUT_OF_SERVICE"})
  private String status;
}
