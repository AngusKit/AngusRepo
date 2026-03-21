package cloud.xcan.angus.core.gm.interfaces.email.facade.vo;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "邮件模板状态更新响应VO")
public class EmailTemplateStatusVo {

  @Schema(description = "模板ID")
  private Long id;

  @Schema(description = "状态")
  private EnabledStatus status;

  @Schema(description = "修改时间")
  private LocalDateTime modifiedDate;
}
