package cloud.xcan.angus.core.gm.interfaces.sms.facade.vo;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.remote.vo.AuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "短信模板VO")
public class SmsTemplateVo extends AuditingVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "模板名称")
  private String name;

  @Schema(description = "模板编码")
  private String code;

  @Schema(description = "语言")
  private Language language;

  @Schema(description = "模板内容")
  private String content;

  @Schema(description = "模板参数")
  private List<String> params;

  @Schema(description = "状态")
  private EnabledStatus status;

  @Schema(description = "使用次数")
  private Long usageCount;

  @Schema(description = "服务商")
  private String provider;

  @Schema(description = "服务商模板编码")
  private String templateCode;

  @Schema(description = "签名")
  private String signature;
}
