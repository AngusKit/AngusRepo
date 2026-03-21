package cloud.xcan.angus.core.gm.interfaces.log.facade.dto;

import cloud.xcan.angus.core.gm.domain.log.enums.LogLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "系统日志内容查询DTO")
public class SystemLogContentDto {

  @Schema(description = "页码，默认1")
  private Integer page = 1;

  @Schema(description = "每页行数，默认100")
  private Integer size = 100;

  @Schema(description = "搜索关键词")
  private String keyword;

  @Schema(description = "日志级别：DEBUG/INFO/WARN/ERROR")
  private LogLevel level;

  @Schema(description = "起始行号（优先级高于page）")
  private Integer startLine;

  @Schema(description = "结束行号")
  private Integer endLine;

  @Schema(description = "是否使用tail模式（从文件末尾读取），默认false")
  private Boolean tail = false;

  @Schema(description = "tail模式读取的行数（当tail=true时生效，默认使用size值）")
  private Integer tailLines;
}
