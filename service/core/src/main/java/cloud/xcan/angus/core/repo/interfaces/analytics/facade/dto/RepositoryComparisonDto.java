package cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "仓库对比分析查询参数")
public class RepositoryComparisonDto implements Serializable {

  @Schema(description = "仓库ID列表")
  private List<Long> repositoryIds;

  @Schema(description = "统计周期(天)")
  private Integer period = 30;
}
