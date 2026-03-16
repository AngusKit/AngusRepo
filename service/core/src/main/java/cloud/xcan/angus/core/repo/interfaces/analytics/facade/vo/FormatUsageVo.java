package cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "格式使用统计结果")
public class FormatUsageVo implements Serializable {

  @Schema(description = "格式名称")
  private String format;

  @Schema(description = "仓库数量")
  private Long repositoryCount;

  @Schema(description = "制品数量")
  private Long artifactCount;

  @Schema(description = "下载次数")
  private Long downloadCount;

  @Schema(description = "存储大小(字节)")
  private Long storageBytes;

  @Schema(description = "增长率(%)")
  private Double growthRate;
}
