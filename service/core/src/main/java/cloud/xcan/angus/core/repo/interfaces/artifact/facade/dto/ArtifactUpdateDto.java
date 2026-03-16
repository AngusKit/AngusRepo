package cloud.xcan.angus.core.repo.interfaces.artifact.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新制品请求参数")
public class ArtifactUpdateDto implements Serializable {

  @Size(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "制品名称")
  private String name;

  @Size(max = 2000)
  @Schema(description = "制品描述")
  private String description;

  @Size(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "许可证")
  private String license;

  @Schema(description = "标签（JSON）")
  private String tags;

  @Schema(description = "元数据（JSON）")
  private String metadata;
}
