package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "接口服务信息")
public class InterfaceServiceVo implements Serializable {

  @Schema(description = "服务名称")
  private String serviceName;

  @Schema(description = "显示名称")
  private String displayName;

  @Schema(description = "基础URL")
  private String baseUrl;

  @Schema(description = "同步时间")
  private LocalDateTime syncTime;

  @Schema(description = "接口数量")
  private Integer interfaceCount;
}
