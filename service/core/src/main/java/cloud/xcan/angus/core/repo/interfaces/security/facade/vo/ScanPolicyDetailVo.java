package cloud.xcan.angus.core.repo.interfaces.security.facade.vo;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

import cloud.xcan.angus.core.repo.domain.security.ScanType;
import cloud.xcan.angus.core.repo.domain.security.VulnerabilitySeverity;
import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.remote.vo.TenantAuditingVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "扫描策略详情")
public class ScanPolicyDetailVo extends TenantAuditingVo {

  @Schema(description = "策略ID")
  private String id;

  @Schema(description = "策略名称")
  private String name;

  @Schema(description = "策略描述")
  private String description;

  @Schema(description = "仓库ID")
  private String repositoryId;

  @Schema(description = "仓库名称")
  private String repositoryName;

  @Schema(description = "扫描类型")
  private ScanType scanType;

  @Schema(description = "是否启用")
  private Boolean enabled;

  @Schema(description = "推送时自动扫描")
  private Boolean scanOnPush;

  @Schema(description = "定时扫描CRON表达式")
  private String scheduleCron;

  @Schema(description = "严重程度阈值")
  private VulnerabilitySeverity severityThreshold;

  @Schema(description = "自动阻止")
  private Boolean autoBlock;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "最后扫描时间")
  private LocalDateTime lastScanTime;
}
