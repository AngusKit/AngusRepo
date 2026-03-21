package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.remote.vo.AuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "短信服务商VO")
public class SmsProviderVo extends AuditingVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "服务商名称")
  private String name;

  @Schema(description = "是否默认")
  private Boolean isDefault;

  @Schema(description = "是否启用")
  private EnabledStatus status;

  @Schema(description = "配置信息")
  private Map<String, String> config;

}
