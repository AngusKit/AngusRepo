package cloud.xcan.angus.core.gm.interfaces.quota.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "配额使用情况VO")
public class QuotaUsageVo implements Serializable {

  @Schema(description = "配额ID")
  private String id;

  @Schema(description = "资源编码")
  private String code;

  @Schema(description = "资源名称")
  private String name;

  @Schema(description = "配额限额")
  private Long limit;

  @Schema(description = "已使用量")
  private Long used;

  @Schema(description = "可用量")
  private Long available;

  @Schema(description = "单位")
  private String unit;

  @Schema(description = "使用率（百分比）")
  private Double usagePercentage;

  @Schema(description = "状态：NORMAL-正常, WARNING-警告(75-90%), CRITICAL-严重(>90%)")
  private String status;

  @Schema(description = "最后刷新时间")
  private LocalDateTime lastRefreshTime;
}
