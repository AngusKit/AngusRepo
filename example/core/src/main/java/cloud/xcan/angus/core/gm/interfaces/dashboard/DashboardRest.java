package cloud.xcan.angus.core.gm.interfaces.dashboard;

import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.interfaces.dashboard.facade.DashboardFacade;
import cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo.DashboardStatisticsVo;
import cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo.RecentActivitiesVo;
import cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo.SystemResourcesVo;
import cloud.xcan.angus.core.gm.interfaces.dashboard.facade.vo.UserGrowthVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard", description = "概览 - 系统整体运行状态的可视化展示")
@Validated
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardRest {

  @Resource
  private DashboardFacade dashboardFacade;

  @Operation(operationId = "getStatistics", summary = "查询概览统计数据",
      description = "获取系统概览页面顶部的统计卡片数据，包括用户、租户、操作、通知四大核心指标")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<DashboardStatisticsVo> getStatistics() {
    return ApiLocaleResult.success(dashboardFacade.getStatistics());
  }

  @Operation(operationId = "getUserGrowth", summary = "查询用户增长趋势",
      description = "获取不同时间维度的用户增长趋势数据，用于展示用户增长曲线图表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/user-growth")
  public ApiLocaleResult<UserGrowthVo> getUserGrowth(
      @Parameter(name = "timeRange", description = "时间范围：7DAYS, 30DAYS, 90DAYS, 6MONTHS, 1YEAR, ALL", required = true)
      @RequestParam(defaultValue = "6MONTHS") String timeRange) {
    return ApiLocaleResult.success(dashboardFacade.getUserGrowth(timeRange));
  }

  @Operation(operationId = "getSystemResources", summary = "查询系统资源使用情况",
      description = "获取系统CPU、内存、磁盘、网络等资源的实时使用情况")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/system-resources")
  public ApiLocaleResult<SystemResourcesVo> getSystemResources() {
    return ApiLocaleResult.success(dashboardFacade.getSystemResources());
  }

  @Operation(operationId = "getRecentActivities", summary = "查询最近活动列表",
      description = "获取系统最近的活动记录，包括用户操作、系统事件等")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/recent-activities")
  public ApiLocaleResult<RecentActivitiesVo> getRecentActivities(
      @Parameter(name = "limit", description = "返回记录数量", required = false)
      @RequestParam(defaultValue = "10") Integer limit,
      @Parameter(name = "resourceType", description = "资源类型筛选（USER, TENANT, ORGANIZATION, PERMISSION, APPLICATION, CONFIG, QUOTA, SYSTEM_EVENT, OTHER）", required = false)
      @RequestParam(required = false) ResourceType resourceType) {
    return ApiLocaleResult.success(dashboardFacade.getRecentActivities(limit, resourceType));
  }
}

