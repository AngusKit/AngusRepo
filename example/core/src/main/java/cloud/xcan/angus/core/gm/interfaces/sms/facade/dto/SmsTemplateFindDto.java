package cloud.xcan.angus.core.gm.interfaces.sms.facade.dto;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "短信模板查询DTO")
public class SmsTemplateFindDto extends PageQuery {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "模板名称")
  private String name;

  @Schema(description = "模板编码")
  private String code;

  @Schema(description = "提供商")
  private String provider;

  @Schema(description = "状态筛选")
  private EnabledStatus status;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
