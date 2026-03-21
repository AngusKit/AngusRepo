package cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.tenant.enums.AccountType;
import cloud.xcan.angus.api.commonlink.tenant.enums.TenantType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "租户查询DTO")
public class TenantFindDto extends PageQuery {

  @Schema(description = "租户ID")
  private Long id;

  @Schema(description = "租户名称")
  private String name;

  @Schema(description = "租户编码")
  private String code;

  @Schema(description = "状态筛选")
  private EnabledStatus status;

  @Schema(description = "类型筛选")
  private TenantType type;

  @Schema(description = "账号类型")
  private AccountType accountType;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
