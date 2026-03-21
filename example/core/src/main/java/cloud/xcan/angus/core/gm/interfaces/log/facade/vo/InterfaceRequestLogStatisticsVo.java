package cloud.xcan.angus.core.gm.interfaces.log.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "API请求日志统计数据")
public class InterfaceRequestLogStatisticsVo {

  @Schema(description = "总请求次数")
  private Long totalCount;

  @Schema(description = "成功请求次数")
  private Long successCount;

  @Schema(description = "失败请求次数")
  private Long errorCount;

  @Schema(description = "成功率（百分比）")
  private Double successRate;

  @Schema(description = "平均响应时间（ms）")
  private Double avgResponseTime;

  @Schema(description = "各请求方法统计")
  private Map<String, Long> methodStatistics;

  @Schema(description = "各状态码范围统计")
  private Map<String, Long> statusStatistics;

  @Schema(description = "请求最频繁的端点TOP10")
  private List<TopEndpointVo> topEndpoints;

  @Schema(description = "请求最频繁的API密钥TOP10")
  private List<TopApiKeyVo> topApiKeys;

  @Data
  @Schema(description = "请求最频繁的端点")
  public static class TopEndpointVo {

    @Schema(description = "端点")
    private String endpoint;

    @Schema(description = "请求次数")
    private Long count;

    @Schema(description = "平均响应时间（ms）")
    private Double avgResponseTime;
  }

  @Data
  @Schema(description = "请求最频繁的API密钥")
  public static class TopApiKeyVo {

    @Schema(description = "API密钥ID")
    private String apiKeyId;

    @Schema(description = "API密钥（脱敏）")
    private String apiKey;

    @Schema(description = "请求次数")
    private Long count;
  }
}
