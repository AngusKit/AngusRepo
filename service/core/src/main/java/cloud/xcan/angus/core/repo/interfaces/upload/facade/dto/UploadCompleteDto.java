package cloud.xcan.angus.core.repo.interfaces.upload.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "完成上传任务请求参数")
public class UploadCompleteDto implements Serializable {

  @Length(max = MAX_CONTENT_LENGTH)
  @Schema(description = "制品描述")
  private String description;

  @Size(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "许可证")
  private String license;

  @Length(max = MAX_LONG_DESC_LENGTH)
  @Schema(description = "标签（逗号分隔）")
  private String tags;

  @Length(max = MAX_METADATA_LENGTH)
  @Schema(description = "元数据（JSON格式）")
  private String metadata;
}
