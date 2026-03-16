package cloud.xcan.angus.core.repo.interfaces.security.facade.dto;

import cloud.xcan.angus.core.repo.domain.security.ScanType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "创建扫描任务请求参数")
public class ScanTaskCreateDto implements Serializable {

  @NotBlank
  @Schema(description = "制品ID", requiredMode = Schema.RequiredMode.REQUIRED)
  private String artifactId;

  @NotBlank
  @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED)
  private String repositoryId;

  @NotNull
  @Schema(description = "扫描类型", requiredMode = Schema.RequiredMode.REQUIRED)
  private ScanType scanType;
}
