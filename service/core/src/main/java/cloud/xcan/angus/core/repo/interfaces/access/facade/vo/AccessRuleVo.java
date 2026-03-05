package cloud.xcan.angus.core.repo.interfaces.access.facade.vo;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

import cloud.xcan.angus.core.repo.domain.access.AccessPrincipalType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "访问规则详情")
public class AccessRuleVo {

  @Schema(description = "规则ID")
  private Long id;

  @Schema(description = "仓库ID")
  private Long repositoryId;

  @Schema(description = "规则名称")
  private String name;

  @Schema(description = "规则描述")
  private String description;

  @Schema(description = "主体类型")
  private AccessPrincipalType principalType;

  @Schema(description = "主体ID")
  private String principalId;

  @Schema(description = "主体名称")
  private String principalName;

  @Schema(description = "是否启用")
  private Boolean enabled;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "过期时间")
  private LocalDateTime expiresAt;

  @Schema(description = "优先级")
  private Integer priority;

  @Schema(description = "权限列表（JSON）")
  private String permissions;

  @Schema(description = "路径列表（JSON）")
  private String paths;

  @Schema(description = "创建人ID")
  private Long createdBy;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "创建时间")
  private LocalDateTime createdDate;

  @Schema(description = "修改人ID")
  private Long modifiedBy;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "修改时间")
  private LocalDateTime modifiedDate;
}
