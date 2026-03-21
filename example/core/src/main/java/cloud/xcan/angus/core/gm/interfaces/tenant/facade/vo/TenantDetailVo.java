package cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.api.commonlink.tenant.enums.AccountType;
import cloud.xcan.angus.api.commonlink.tenant.enums.TenantType;
import cloud.xcan.angus.remote.vo.AuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "租户详情")
public class TenantDetailVo extends AuditingVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "租户名称")
  private String name;

  @Schema(description = "租户编码")
  private String code;

  @Schema(description = "租户类型")
  private TenantType type;

  @Schema(description = "账号类型")
  private AccountType accountType;

  @Schema(description = "管理员姓名")
  private String adminName;

  @Schema(description = "管理员邮箱")
  private String adminEmail;

  @Schema(description = "管理员电话")
  private String adminPhone;

  @Schema(description = "当前租户用户数量")
  private Long userCount;

  @Schema(description = "部门数量")
  private Long departmentCount;

  @Schema(description = "主账号（租户）下子账号数")
  private Long subTenantCount;

  @Schema(description = "状态")
  private EnabledStatus status;

  @Schema(description = "地址")
  private String address;

  @Schema(description = "过期日期")
  private LocalDateTime expireDate;

  @Schema(description = "Logo")
  private String logo;

  @Schema(description = "默认语言")
  private Language defaultLanguage;
}
