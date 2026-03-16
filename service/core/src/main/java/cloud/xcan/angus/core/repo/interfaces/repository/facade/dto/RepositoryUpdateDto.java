package cloud.xcan.angus.core.repo.interfaces.repository.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;
import static cloud.xcan.angus.spec.experimental.BizConstant.*;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新仓库请求参数")
public class RepositoryUpdateDto implements Serializable {

  @Size(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "仓库名称")
  private String name;

  @Size(max = 2000)
  @Schema(description = "仓库描述")
  private String description;

  @Schema(description = "远程仓库URL")
  private String remoteUrl;

  @Schema(description = "仓库设置（JSON）")
  private String settings;
}
