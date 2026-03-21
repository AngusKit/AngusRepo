package cloud.xcan.angus.core.gm.interfaces.email.facade.dto;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "邮件模板查询DTO")
public class EmailTemplateFindDto extends PageQuery {

  @Schema(description = "模板ID")
  private Long id;

  @Schema(description = "模板名称")
  private String name;

  @Schema(description = "模板编码")
  private String code;

  @Schema(description = "语言")
  private Language language;

  @Schema(description = "邮件主题")
  private String subject;

  @Schema(description = "状态筛选（已启用、已禁用）")
  private EnabledStatus status;

}
