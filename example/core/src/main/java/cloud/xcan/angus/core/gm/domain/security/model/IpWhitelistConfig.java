package cloud.xcan.angus.core.gm.domain.security.model;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "IP白名单配置")
public class IpWhitelistConfig extends SecurityConfig {

  @Schema(description = "IP地址", example = "192.168.1.100")
  private String ipAddress;

  @Schema(description = "IP范围", example = "192.168.1.0/24")
  private String ipRange;

  @Schema(description = "描述", example = "办公室网络")
  private String description;

  @Schema(description = "状态", example = "ENABLED")
  private EnabledStatus status;

  @Schema(description = "最后使用时间", example = "2025-12-19T10:30:00")
  private LocalDateTime lastUsed;

  @Schema(description = "使用次数", example = "150")
  private Long usageCount;
}
