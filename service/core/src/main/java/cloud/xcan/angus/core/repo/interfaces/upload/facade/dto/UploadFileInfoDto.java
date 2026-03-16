package cloud.xcan.angus.core.repo.interfaces.upload.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "批量上传文件信息")
public class UploadFileInfoDto implements Serializable {

  @NotBlank
  @Size(max = 500)
  @Schema(description = "文件名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String fileName;

  @NotNull
  @Schema(description = "文件大小（字节）", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long fileSize;

  @Size(max = 1000)
  @Schema(description = "上传路径")
  private String path;

  @Size(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "制品版本号")
  private String version;
}
