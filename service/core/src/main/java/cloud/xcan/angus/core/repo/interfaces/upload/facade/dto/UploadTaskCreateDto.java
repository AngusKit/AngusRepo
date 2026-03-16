package cloud.xcan.angus.core.repo.interfaces.upload.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;
import static cloud.xcan.angus.core.repo.domain.Constants.*;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "创建上传任务请求参数")
public class UploadTaskCreateDto implements Serializable {

  @NotNull
  @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long repositoryId;

  @NotBlank
  @Length(max = MAX_LONG_DESC_LENGTH)
  @Schema(description = "文件名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String fileName;

  @NotNull
  @Schema(description = "文件大小（字节）", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long fileSize;

  @Size(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "文件校验和")
  private String checksum;

  @Length(max = MAX_EXPRESSION_LENGTH)
  @Schema(description = "上传路径")
  private String path;

  @Size(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "制品版本号")
  private String version;

  @Schema(description = "是否启用分片上传")
  private Boolean enableChunked;

  @Schema(description = "分片大小（字节）")
  private Long chunkSize;
}
