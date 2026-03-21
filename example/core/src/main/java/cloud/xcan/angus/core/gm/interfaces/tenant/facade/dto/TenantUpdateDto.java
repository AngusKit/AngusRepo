package cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ADDRESS_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_EMAIL_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_KEY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_MOBILE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH_X2;

import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.api.commonlink.tenant.enums.AccountType;
import cloud.xcan.angus.api.commonlink.tenant.enums.TenantType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "更新租户DTO")
public class TenantUpdateDto {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "租户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "示例租户")
  private String name;

  @NotBlank
  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "租户编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "TENANT001")
  private String code;

  @Schema(description = "租户类型")
  private TenantType type;

  @Schema(description = "账号类型")
  private AccountType accountType;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "管理员姓名", example = "张三")
  private String adminName;

  @Length(max = MAX_EMAIL_LENGTH)
  @Schema(description = "管理员邮箱", example = "admin@example.com")
  private String adminEmail;

  @Length(max = MAX_MOBILE_LENGTH)
  @Schema(description = "管理员电话", example = "13800138000")
  private String adminPhone;

  @Length(max = MAX_ADDRESS_LENGTH)
  @Schema(description = "地址", example = "北京市朝阳区")
  private String address;

  @Schema(description = "过期日期（yyyy-MM-dd HH:MM:SS）", example = "2025-12-31 12:00:00")
  private LocalDateTime expireDate;

  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "Logo URL", example = "https://example.com/logo.png")
  private String logo;

  @Schema(description = "默认语言", example = "zh-CN")
  private Language defaultLanguage;
}
