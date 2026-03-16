package cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "格式使用统计查询参数")
public class FormatUsageDto implements Serializable {

  @Schema(description = "统计周期(天)")
  private Integer period = 30;
}
