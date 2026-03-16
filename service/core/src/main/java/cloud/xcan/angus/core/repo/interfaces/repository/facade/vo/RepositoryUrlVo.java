package cloud.xcan.angus.core.repo.interfaces.repository.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "仓库URL信息")
public class RepositoryUrlVo implements Serializable {

  @Schema(description = "仓库ID")
  private Long id;

  @Schema(description = "仓库名称")
  private String name;

  @Schema(description = "仓库访问URL")
  private String url;
}
