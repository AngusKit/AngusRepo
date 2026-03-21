package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "服务列表")
public class ServiceListVo {

  @Schema(description = "服务名称")
  private String serviceName;

  @Schema(description = "显示名称")
  private String displayName;

  @Schema(description = "服务实例列表")
  private List<ServiceInstanceVo> instances;
}
