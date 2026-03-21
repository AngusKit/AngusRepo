package cloud.xcan.angus.core.gm.interfaces.role.facade.dto;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.remote.PageQuery;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询角色请求参数")
public class RoleFindDto extends PageQuery {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "角色名称")
  private String name;

  @Schema(description = "角色编码")
  private String code;

  @Schema(description = "应用ID筛选")
  private Long appId;

  @Schema(description = "角色状态")
  private EnabledStatus status;

  @Schema(description = "是否系统角色")
  private Boolean isSystem;

  @Schema(description = "是否默认角色")
  private Boolean isDefault;

  @Override
  @JsonIgnore
  @Schema(hidden = true)
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
