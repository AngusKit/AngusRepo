package cloud.xcan.angus.core.repo.interfaces.system.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "连接测试结果")
public class ConnectionTestResultVo {

  @Schema(description = "测试是否成功")
  private Boolean success;

  @Schema(description = "测试消息")
  private String message;

  @Schema(description = "响应时间（毫秒）")
  private Long responseTime;
}
