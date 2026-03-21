package cloud.xcan.angus.core.gm.interfaces.email.facade.dto;

import cloud.xcan.angus.api.commonlink.email.EmailStatus;
import cloud.xcan.angus.core.gm.domain.email.enums.EmailType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "邮件记录查询DTO")
public class EmailRecordFindDto extends PageQuery {

  @Schema(description = "邮件主题")
  private String subject;

  @Schema(description = "邮件状态")
  private EmailStatus status;

  @Schema(description = "邮件类型")
  private EmailType type;

  @Schema(description = "模板ID筛选")
  private Long templateId;

  @Schema(description = "开始日期")
  private LocalDateTime startDate;

  @Schema(description = "结束日期")
  private LocalDateTime endDate;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
