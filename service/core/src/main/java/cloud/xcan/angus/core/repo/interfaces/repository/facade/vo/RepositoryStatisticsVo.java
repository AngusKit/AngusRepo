package cloud.xcan.angus.core.repo.interfaces.repository.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "仓库统计信息")
public class RepositoryStatisticsVo implements Serializable {

  @Schema(description = "仓库总数")
  private Long totalRepositories;

  @Schema(description = "在线仓库数")
  private Long onlineRepositories;

  @Schema(description = "离线仓库数")
  private Long offlineRepositories;

  @Schema(description = "Maven仓库数")
  private Long mavenRepositories;

  @Schema(description = "Docker仓库数")
  private Long dockerRepositories;

  @Schema(description = "NPM仓库数")
  private Long npmRepositories;
}
