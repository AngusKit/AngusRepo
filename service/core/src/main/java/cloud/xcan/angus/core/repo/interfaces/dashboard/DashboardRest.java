package cloud.xcan.angus.core.repo.interfaces.dashboard;

import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.DashboardFacade;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.DashboardOverviewVo;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.RecentActivityVo;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.StorageDistributionVo;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.SystemMetricsVo;
import cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo.TopRepositoryVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard", description = "仪表盘 - 总览统计、Top仓库、最近活动、存储分布、系统指标")
@Validated
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardRest {

  @Resource
  private DashboardFacade dashboardFacade;

  @Operation(summary = "总览统计", operationId = "dashboard:overview")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping("/overview")
  public ApiLocaleResult<DashboardOverviewVo> getOverview() {
    return ApiLocaleResult.success(dashboardFacade.getOverview());
  }

  @Operation(summary = "Top仓库", operationId = "dashboard:topRepositories")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping("/top-repositories")
  public ApiLocaleResult<List<TopRepositoryVo>> getTopRepositories() {
    return ApiLocaleResult.success(dashboardFacade.getTopRepositories());
  }

  @Operation(summary = "最近活动", operationId = "dashboard:recentActivity")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping("/recent-activity")
  public ApiLocaleResult<List<RecentActivityVo>> getRecentActivity() {
    return ApiLocaleResult.success(dashboardFacade.getRecentActivity());
  }

  @Operation(summary = "存储分布", operationId = "dashboard:storageDistribution")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping("/storage-distribution")
  public ApiLocaleResult<List<StorageDistributionVo>> getStorageDistribution() {
    return ApiLocaleResult.success(dashboardFacade.getStorageDistribution());
  }

  @Operation(summary = "系统指标", operationId = "dashboard:systemMetrics")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping("/system-metrics")
  public ApiLocaleResult<SystemMetricsVo> getSystemMetrics() {
    return ApiLocaleResult.success(dashboardFacade.getSystemMetrics());
  }
}
