package cloud.xcan.angus.core.gm.interfaces.log.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "系统日志统计数据")
public class SystemLogStatisticsVo {

  @Schema(description = "日志文件总数")
  private Integer totalFiles;

  @Schema(description = "总大小（字节）")
  private Long totalSize;

  @Schema(description = "格式化总大小")
  private String totalSizeFormatted;

  @Schema(description = "各类型日志统计")
  private Map<String, TypeStatisticsVo> typeStatistics;

  @Schema(description = "各状态日志统计")
  private Map<String, Integer> statusStatistics;

  @Schema(description = "各应用日志统计")
  private List<ApplicationStatisticsVo> applicationStatistics;

  @Schema(description = "最早的日志文件")
  private LogFileInfoVo oldestLog;

  @Schema(description = "最新的日志文件")
  private LogFileInfoVo newestLog;

  @Data
  @Schema(description = "类型统计")
  public static class TypeStatisticsVo {

    @Schema(description = "文件数量")
    private Integer count;

    @Schema(description = "总大小（字节）")
    private Long size;

    @Schema(description = "格式化大小")
    private String sizeFormatted;
  }

  @Data
  @Schema(description = "应用统计")
  public static class ApplicationStatisticsVo {

    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "应用名称")
    private String applicationName;

    @Schema(description = "文件数量")
    private Integer fileCount;

    @Schema(description = "总大小（字节）")
    private Long totalSize;

    @Schema(description = "格式化总大小")
    private String totalSizeFormatted;
  }

  @Data
  @Schema(description = "日志文件信息")
  public static class LogFileInfoVo {

    @Schema(description = "文件名")
    private String filename;

    @Schema(description = "日期")
    private String date;
  }
}
