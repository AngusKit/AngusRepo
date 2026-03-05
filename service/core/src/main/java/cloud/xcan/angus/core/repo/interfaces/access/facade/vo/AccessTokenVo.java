package cloud.xcan.angus.core.repo.interfaces.access.facade.vo;

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
@Schema(description = "访问令牌详情")
public class AccessTokenVo {

  @Schema(description = "令牌ID")
  private Long id;

  @Schema(description = "仓库ID")
  private Long repositoryId;

  @Schema(description = "令牌名称")
  private String name;

  @Schema(description = "令牌描述")
  private String description;

  @Schema(description = "令牌（仅创建时返回）")
  private String token;

  @Schema(description = "是否启用")
  private Boolean enabled;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "过期时间")
  private LocalDateTime expiresAt;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "最后使用时间")
  private LocalDateTime lastUsed;

  @Schema(description = "使用次数")
  private Long usageCount;

  @Schema(description = "权限列表（JSON）")
  private String permissions;

  @Schema(description = "IP白名单（JSON）")
  private String ipWhitelist;

  @Schema(description = "创建人ID")
  private Long createdBy;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "创建时间")
  private LocalDateTime createdDate;
}
