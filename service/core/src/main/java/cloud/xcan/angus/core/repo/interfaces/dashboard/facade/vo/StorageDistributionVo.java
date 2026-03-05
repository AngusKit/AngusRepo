package cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "存储分布")
public class StorageDistributionVo {

  @Schema(description = "格式")
  private String format;

  @Schema(description = "存储大小(字节)")
  private Long storageBytes;

  @Schema(description = "制品数量")
  private Long artifactCount;

  @Schema(description = "占比(%)")
  private Double percentage;
}
