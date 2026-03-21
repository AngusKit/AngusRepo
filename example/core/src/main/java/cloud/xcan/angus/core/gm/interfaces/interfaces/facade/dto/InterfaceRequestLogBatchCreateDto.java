package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "批量创建API请求日志DTO")
public class InterfaceRequestLogBatchCreateDto {

  @NotEmpty
  @Length(max = MAX_BATCH_SIZE)
  @Valid
  @Schema(description = "日志记录列表", requiredMode = RequiredMode.REQUIRED)
  private List<InterfaceRequestLogCreateDto> logs;
}
