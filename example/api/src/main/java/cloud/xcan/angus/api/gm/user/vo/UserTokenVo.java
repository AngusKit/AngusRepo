package cloud.xcan.angus.api.gm.user.vo;

import cloud.xcan.angus.api.commonlink.user.enums.TokenStatus;
import cloud.xcan.angus.remote.vo.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户令牌详情")
public class UserTokenVo extends TenantAuditingVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "用户ID")
  private Long userId;

  @Schema(description = "令牌名称")
  private String name;

  @Schema(description = "令牌描述")
  private String description;

  @Schema(description = "令牌值（创建时返回完整值，列表和详情中显示掩码）")
  private String token;

  @Schema(description = "授权应用ID")
  private Long appId;

  @Schema(description = "应用名称")
  private String appCode;

  @Schema(description = "权限范围列表")
  private List<String> scopes;

  @Schema(description = "过期时间")
  private LocalDateTime expiresAt;

  @Schema(description = "状态")
  private TokenStatus status;

  @Schema(description = "最后使用时间")
  private LocalDateTime lastUsedAt;

  @Schema(description = "使用次数")
  private Integer usageCount;

  @Schema(description = "撤销时间")
  private LocalDateTime revokedAt;
}
