package cloud.xcan.angus.core.repo.interfaces.format.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "格式验证结果")
public class FormatValidationResultVo {

  @Schema(description = "是否验证通过")
  private Boolean valid;

  @Schema(description = "错误信息列表")
  private List<String> errors;

  @Schema(description = "警告信息列表")
  private List<String> warnings;
}
