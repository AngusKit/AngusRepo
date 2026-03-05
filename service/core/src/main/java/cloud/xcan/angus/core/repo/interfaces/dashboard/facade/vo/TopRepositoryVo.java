package cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "Top仓库")
public class TopRepositoryVo {

  @Schema(description = "仓库ID")
  private Long id;

  @Schema(description = "仓库名称")
  private String name;

  @Schema(description = "格式")
  private String format;

  @Schema(description = "制品数")
  private Long artifactCount;

  @Schema(description = "下载次数")
  private Long downloadCount;

  @Schema(description = "存储大小(字节)")
  private Long storageBytes;
}
