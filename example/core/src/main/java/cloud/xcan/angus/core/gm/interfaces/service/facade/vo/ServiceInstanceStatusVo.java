package cloud.xcan.angus.core.gm.interfaces.service.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "服务实例状态")
public class ServiceInstanceStatusVo implements Serializable {

  @Schema(description = "实例ID")
  private String instanceId;

  @Schema(description = "状态")
  private String status;

  @Schema(description = "修改时间")
  private LocalDateTime modifiedDate;
}
