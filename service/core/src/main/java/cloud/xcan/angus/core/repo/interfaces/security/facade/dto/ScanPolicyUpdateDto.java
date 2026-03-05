package cloud.xcan.angus.core.repo.interfaces.security.facade.dto;

import cloud.xcan.angus.core.repo.domain.security.ScanType;
import cloud.xcan.angus.core.repo.domain.security.VulnerabilitySeverity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新扫描策略请求参数")
public class ScanPolicyUpdateDto {

  @Size(max = 255)
  @Schema(description = "策略名称")
  private String name;

  @Size(max = 1000)
  @Schema(description = "策略描述")
  private String description;

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
}
