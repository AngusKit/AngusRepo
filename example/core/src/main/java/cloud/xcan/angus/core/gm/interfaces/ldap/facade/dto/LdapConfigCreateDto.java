package cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto;

import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "LDAP配置创建DTO")
public class LdapConfigCreateDto implements Serializable {

  @NotBlank
  @Schema(description = "配置名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @NotNull
  @Schema(description = "LDAP类型", requiredMode = Schema.RequiredMode.REQUIRED)
  private LdapType type;

  @NotBlank
  @Schema(description = "LDAP服务器地址", requiredMode = Schema.RequiredMode.REQUIRED)
  private String server;

  @NotBlank
  @Schema(description = "基础DN", requiredMode = Schema.RequiredMode.REQUIRED)
  private String baseDN;

  @NotBlank
  @Schema(description = "绑定DN", requiredMode = Schema.RequiredMode.REQUIRED)
  private String bindDN;

  @Schema(description = "绑定密码")
  private String bindPassword;

  @Schema(description = "用户搜索基础")
  private String userSearchBase;

  @Schema(description = "用户搜索过滤器")
  private String userSearchFilter;

  @Schema(description = "组搜索基础")
  private String groupSearchBase;

  @Schema(description = "组搜索过滤器")
  private String groupSearchFilter;

  @Schema(description = "是否使用SSL")
  private Boolean useSsl;

  @Schema(description = "是否启用")
  private Boolean isEnabled;

  @Schema(description = "是否启用同步")
  private Boolean syncEnabled;

  @Schema(description = "同步间隔（秒）")
  private Integer syncInterval;

  @Schema(description = "描述")
  private String description;

  @Schema(description = "字段映射：系统字段->LDAP属性")
  private Map<String, String> fieldMapping;
}
