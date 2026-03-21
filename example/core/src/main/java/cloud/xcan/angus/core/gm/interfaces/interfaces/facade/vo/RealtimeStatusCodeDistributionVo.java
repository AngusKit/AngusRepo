package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "实时HTTP状态码分布VO")
public class RealtimeStatusCodeDistributionVo implements Serializable {

  @Schema(description = "状态码分布，key为2xx/4xx/5xx/other，value为数量")
  private Map<String, Long> distribution;
}
