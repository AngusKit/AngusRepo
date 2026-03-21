package cloud.xcan.angus.core.gm.interfaces.quota.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "资源配额查询DTO")
public class QuotaFindDto {

  @Schema(description = "应用ID筛选")
  private String appCode;

  @Schema(description = "启用状态筛选")
  private Boolean enabled;

}
