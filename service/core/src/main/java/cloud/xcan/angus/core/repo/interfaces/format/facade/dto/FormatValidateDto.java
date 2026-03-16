package cloud.xcan.angus.core.repo.interfaces.format.facade.dto;

import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
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
@Schema(description = "格式验证请求参数")
public class FormatValidateDto implements Serializable {

  @NotNull
  @Schema(description = "仓库格式", requiredMode = Schema.RequiredMode.REQUIRED)
  private RepositoryFormat format;

  @NotBlank
  @Schema(description = "文件名", requiredMode = Schema.RequiredMode.REQUIRED)
  private String fileName;
}
