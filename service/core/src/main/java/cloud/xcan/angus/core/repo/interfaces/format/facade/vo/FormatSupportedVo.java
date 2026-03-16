package cloud.xcan.angus.core.repo.interfaces.format.facade.vo;

import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "支持的仓库格式信息")
public class FormatSupportedVo implements Serializable {

  @Schema(description = "格式类型")
  private RepositoryFormat format;

  @Schema(description = "格式名称")
  private String name;

  @Schema(description = "是否支持")
  private Boolean supported;
}
