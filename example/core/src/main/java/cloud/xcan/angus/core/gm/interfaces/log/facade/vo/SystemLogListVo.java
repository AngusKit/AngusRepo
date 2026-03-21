package cloud.xcan.angus.core.gm.interfaces.log.facade.vo;

import cloud.xcan.angus.core.gm.domain.log.enums.LogStatus;
import cloud.xcan.angus.core.gm.domain.log.enums.LogType;
import cloud.xcan.angus.remote.NameJoinField;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "系统日志列表VO")
public class SystemLogListVo {

  @Schema(description = "日志文件ID")
  private Long id;

  @Schema(description = "文件名")
  private String filename;

  @Schema(description = "文件大小（字节）")
  private Long size;

  @Schema(description = "格式化文件大小")
  private String sizeFormatted;

  @Schema(description = "行数")
  private Long lineCount;

  @Schema(description = "日志类型枚举值")
  private LogType type;

  @Schema(description = "日志日期")
  private LocalDate date;

  @Schema(description = "应用ID")
  private Long applicationId;

  @Schema(description = "应用名称")
  @NameJoinField(id = "applicationId", repository = "applicationRepo")
  private String applicationName;

  @Schema(description = "状态枚举值")
  private LogStatus status;

  @Schema(description = "是否压缩")
  private Boolean compressed;

  @Schema(description = "创建文件时间")
  private LocalDateTime createdDate;
}
