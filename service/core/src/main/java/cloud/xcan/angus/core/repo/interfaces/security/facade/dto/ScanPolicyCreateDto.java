package cloud.xcan.angus.core.repo.interfaces.security.facade.dto;

import cloud.xcan.angus.core.repo.domain.security.ScanType;
import cloud.xcan.angus.core.repo.domain.security.VulnerabilitySeverity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "创建扫描策略请求参数")
public class ScanPolicyCreateDto {

  @NotBlank
  @Size(max = 255)
  @Schema(description = "策略名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Size(max = 1000)
  @Schema(description = "策略描述")
  private String description;

  @NotBlank
  @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED)
  private String repositoryId;

  @NotNull
  @Schema(description = "扫描类型", requiredMode = Schema.RequiredMode.REQUIRED)
  private ScanType scanType;

  @Schema(description = "是否启用")
  private Boolean enabled = true;

  @Schema(description = "推送时自动扫描")
  private Boolean scanOnPush = false;

  @Schema(description = "定时扫描CRON表达式")
  private String scheduleCron;

  @Schema(description = "严重程度阈值")
  private VulnerabilitySeverity severityThreshold;

  @Schema(description = "自动阻止")
  private Boolean autoBlock = false;
}
