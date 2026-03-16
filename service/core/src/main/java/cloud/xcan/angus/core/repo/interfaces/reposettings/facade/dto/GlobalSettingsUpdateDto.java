package cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;
import static cloud.xcan.angus.spec.experimental.BizConstant.*;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新全局仓库设置请求参数")
public class GlobalSettingsUpdateDto implements Serializable {

  @Size(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "默认仓库")
  private String defaultRepository;

  @Schema(description = "是否允许匿名访问")
  private Boolean anonymousAccess;

  @Schema(description = "是否启用索引")
  private Boolean indexingEnabled;

  @Schema(description = "是否启用压缩")
  private Boolean compressionEnabled;

  @Min(0)
  @Schema(description = "存储配额（GB）")
  private Long storageQuotaGb;

  @Min(0)
  @Schema(description = "保留天数")
  private Integer retentionDays;

  @Schema(description = "是否自动清理")
  private Boolean autoCleanup;

  @Schema(description = "是否启用去重")
  private Boolean deduplicationEnabled;
}
