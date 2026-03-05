package cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "全局仓库设置详情")
public class GlobalSettingsVo {

  @Schema(description = "设置ID")
  private Long id;

  @Schema(description = "默认仓库")
  private String defaultRepository;

  @Schema(description = "是否允许匿名访问")
  private Boolean anonymousAccess;

  @Schema(description = "是否启用索引")
  private Boolean indexingEnabled;

  @Schema(description = "是否启用压缩")
  private Boolean compressionEnabled;

  @Schema(description = "存储配额（GB）")
  private Long storageQuotaGb;

  @Schema(description = "保留天数")
  private Integer retentionDays;

  @Schema(description = "是否自动清理")
  private Boolean autoCleanup;

  @Schema(description = "是否启用去重")
  private Boolean deduplicationEnabled;

  @Schema(description = "修改人ID")
  private Long modifiedBy;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "修改时间")
  private LocalDateTime modifiedDate;
}
