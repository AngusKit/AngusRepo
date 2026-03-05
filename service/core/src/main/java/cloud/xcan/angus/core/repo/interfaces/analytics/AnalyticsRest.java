package cloud.xcan.angus.core.repo.interfaces.analytics;

import cloud.xcan.angus.core.repo.interfaces.analytics.facade.AnalyticsFacade;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto.AnalyticsExportDto;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto.DownloadAnalyticsDto;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto.FormatUsageDto;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto.RepositoryComparisonDto;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.dto.UserActivityAnalyticsDto;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.DownloadAnalyticsVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.FormatUsageVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.RepositoryComparisonVo;
import cloud.xcan.angus.core.repo.interfaces.analytics.facade.vo.UserActivityAnalyticsVo;
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

@Tag(name = "Analytics", description = "分析统计 - 下载分析、用户活跃度、仓库对比、报告导出")
@Validated
@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsRest {

  @Resource
  private AnalyticsFacade analyticsFacade;

  @Operation(summary = "下载分析", operationId = "analytics:downloads")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping("/downloads")
  public ApiLocaleResult<DownloadAnalyticsVo> getDownloadAnalytics(@ParameterObject DownloadAnalyticsDto dto) {
    return ApiLocaleResult.success(analyticsFacade.getDownloadAnalytics(dto));
  }

  @Operation(summary = "用户活跃度分析", operationId = "analytics:userActivity")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping("/user-activity")
  public ApiLocaleResult<UserActivityAnalyticsVo> getUserActivityAnalytics(@ParameterObject UserActivityAnalyticsDto dto) {
    return ApiLocaleResult.success(analyticsFacade.getUserActivityAnalytics(dto));
  }

  @Operation(summary = "仓库对比分析", operationId = "analytics:repositoryComparison")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping("/repository-comparison")
  public ApiLocaleResult<List<RepositoryComparisonVo>> getRepositoryComparison(@ParameterObject RepositoryComparisonDto dto) {
    return ApiLocaleResult.success(analyticsFacade.getRepositoryComparison(dto));
  }

  @Operation(summary = "导出分析报告", operationId = "analytics:export")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "导出任务已创建")})
  @GetMapping("/export")
  public ApiLocaleResult<String> exportReport(@ParameterObject AnalyticsExportDto dto) {
    return ApiLocaleResult.success(analyticsFacade.exportReport(dto));
  }

  @Operation(summary = "格式使用统计", operationId = "analytics:formatUsage")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping("/format-usage")
  public ApiLocaleResult<List<FormatUsageVo>> getFormatUsage(@ParameterObject FormatUsageDto dto) {
    return ApiLocaleResult.success(analyticsFacade.getFormatUsage(dto));
  }
}
