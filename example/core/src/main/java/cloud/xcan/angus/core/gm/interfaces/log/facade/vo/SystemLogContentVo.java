package cloud.xcan.angus.core.gm.interfaces.log.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "系统日志内容VO")
public class SystemLogContentVo {

  @Schema(description = "日志文件ID")
  private Long id;

  @Schema(description = "文件名")
  private String filename;

  @Schema(description = "总行数")
  private Long totalLines;

  @Schema(description = "当前页码")
  private Integer currentPage;

  @Schema(description = "每页行数")
  private Integer pageSize;

  @Schema(description = "总页数")
  private Integer totalPages;

  @Schema(description = "日志行列表")
  private List<LogLineVo> lines;

  @Data
  @Schema(description = "日志行")
  public static class LogLineVo {

    @Schema(description = "行号")
    private Integer lineNumber;

    @Schema(description = "时间戳")
    private String timestamp;

    @Schema(description = "日志级别")
    private String level;

    @Schema(description = "线程名")
    private String thread;

    @Schema(description = "Logger名称")
    private String logger;

    @Schema(description = "日志消息")
    private String message;

    @Schema(description = "堆栈跟踪（如有）")
    private String stackTrace;

    @Schema(description = "原始日志行（用于原样展示）")
    private String rawLine;
  }
}
