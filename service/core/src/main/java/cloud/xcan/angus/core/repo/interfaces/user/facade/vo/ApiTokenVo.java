package cloud.xcan.angus.core.repo.interfaces.user.facade.vo;

import cloud.xcan.angus.core.repo.domain.user.TokenPermission;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "API Token信息")
public class ApiTokenVo {

  @Schema(description = "Token ID")
  private Long id;

  @Schema(description = "Token名称")
  private String name;

  @Schema(description = "Token描述")
  private String description;

  @Schema(description = "Token权限")
  private TokenPermission permission;

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

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "创建时间")
  private LocalDateTime createdDate;

  @Schema(description = "Token值（仅创建时返回）")
  private String token;
}
