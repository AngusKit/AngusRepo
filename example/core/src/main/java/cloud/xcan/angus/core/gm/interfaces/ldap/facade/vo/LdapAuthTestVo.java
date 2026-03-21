package cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "LDAP认证测试结果VO")
public class LdapAuthTestVo implements Serializable {

  @Schema(description = "是否认证成功")
  private Boolean authenticated;

  @Schema(description = "用户DN")
  private String userDN;

  @Schema(description = "用户属性")
  private Map<String, String> attributes;
}
