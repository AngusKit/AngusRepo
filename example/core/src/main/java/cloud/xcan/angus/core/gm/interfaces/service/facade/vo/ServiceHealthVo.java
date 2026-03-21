package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "服务健康状态")
public class ServiceHealthVo {

  @Schema(description = "状态")
  private String status;

  @Schema(description = "详细信息")
  private Map<String, Object> details;
}
