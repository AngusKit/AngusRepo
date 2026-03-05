package cloud.xcan.angus.core.repo.interfaces.analytics;

import cloud.xcan.angus.core.repo.interfaces.analytics.facade.TrendAnalyticsFacade;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto.TrendQueryDto;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.FormatDistributionVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.TrendDataPointVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.TrendingArtifactVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.TrendingRepositoryVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "TrendAnalytics", description = "趋势分析 - 下载/上传/存储趋势、热门制品/仓库排行")
@Validated
@RestController
@RequestMapping("/api/v1/analytics")
public class TrendAnalyticsRest {

  @Resource
  private TrendAnalyticsFacade trendAnalyticsFacade;

  @Operation(summary = "热门制品排行", operationId = "analytics:trendingArtifacts")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping("/trending-artifacts")
  public ApiLocaleResult<List<TrendingArtifactVo>> getTrendingArtifacts() {
    return ApiLocaleResult.success(trendAnalyticsFacade.getTrendingArtifacts());
  }

  @Operation(summary = "热门仓库排行", operationId = "analytics:trendingRepositories")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping("/trending-repositories")
  public ApiLocaleResult<List<TrendingRepositoryVo>> getTrendingRepositories() {
    return ApiLocaleResult.success(trendAnalyticsFacade.getTrendingRepositories());
  }

  @Operation(summary = "下载趋势", operationId = "analytics:downloadTrend")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping("/download-trend")
  public ApiLocaleResult<List<TrendDataPointVo>> getDownloadTrend(@ParameterObject TrendQueryDto dto) {
    return ApiLocaleResult.success(trendAnalyticsFacade.getDownloadTrend(dto));
  }

  @Operation(summary = "上传趋势", operationId = "analytics:uploadTrend")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping("/upload-trend")
  public ApiLocaleResult<List<TrendDataPointVo>> getUploadTrend(@ParameterObject TrendQueryDto dto) {
    return ApiLocaleResult.success(trendAnalyticsFacade.getUploadTrend(dto));
  }

  @Operation(summary = "存储增长趋势", operationId = "analytics:storageTrend")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping("/storage-trend")
  public ApiLocaleResult<List<TrendDataPointVo>> getStorageTrend(@ParameterObject TrendQueryDto dto) {
    return ApiLocaleResult.success(trendAnalyticsFacade.getStorageTrend(dto));
  }

  @Operation(summary = "用户活跃度趋势", operationId = "analytics:userActivityTrend")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping("/user-activity-trend")
  public ApiLocaleResult<List<TrendDataPointVo>> getUserActivityTrend(@ParameterObject TrendQueryDto dto) {
    return ApiLocaleResult.success(trendAnalyticsFacade.getUserActivityTrend(dto));
  }

  @Operation(summary = "格式分布", operationId = "analytics:formatDistribution")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping("/format-distribution")
  public ApiLocaleResult<List<FormatDistributionVo>> getFormatDistribution() {
    return ApiLocaleResult.success(trendAnalyticsFacade.getFormatDistribution());
  }
}
