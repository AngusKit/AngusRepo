package cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "LDAP批量同步结果VO")
public class LdapSyncAllResultVo implements Serializable {

  @Schema(description = "各配置的同步结果列表")
  private List<LdapSyncAllResultItemVo> results;

  @Data
  @Schema(description = "单个LDAP配置的同步结果")
  public static class LdapSyncAllResultItemVo implements Serializable {

    @Schema(description = "LDAP配置ID")
    private Long configId;

    @Schema(description = "LDAP配置名称")
    private String configName;

    @Schema(description = "同步结果")
    private LdapSyncResultVo result;
  }
}
