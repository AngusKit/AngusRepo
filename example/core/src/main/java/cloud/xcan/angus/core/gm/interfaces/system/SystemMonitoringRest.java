package cloud.xcan.angus.core.gm.interfaces.system;

import cloud.xcan.angus.core.gm.interfaces.system.facade.SystemMonitoringFacade;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.CpuUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.DiskUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.EnvironmentVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.HealthCheckVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.MemoryUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.MonitoringOverviewVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.NetworkUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.ProcessInfoVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SystemMonitoring", description = "系统监控 - 系统资源监控、性能指标、告警管理")
@Validated
@RestController
@RequestMapping("/api/v1/system/monitoring")
public class SystemMonitoringRest {

  @Resource
  private SystemMonitoringFacade systemMonitoringFacade;

  @Operation(operationId = "getEnvironment", summary = "获取系统环境信息", description = "获取系统运行环境信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/environment")
  public ApiLocaleResult<EnvironmentVo> getEnvironment() {
    return ApiLocaleResult.success(systemMonitoringFacade.getEnvironment());
  }

  @Operation(operationId = "getMonitoringOverview", summary = "获取系统监控概览", description = "获取系统监控整体概览")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/overview")
  public ApiLocaleResult<MonitoringOverviewVo> getOverview() {
    return ApiLocaleResult.success(systemMonitoringFacade.getOverview());
  }

  @Operation(operationId = "getSystemHealth", summary = "获取系统健康检查", description = "获取系统健康状态")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/health")
  public ApiLocaleResult<HealthCheckVo> getHealth() {
    return ApiLocaleResult.success(systemMonitoringFacade.getHealth());
  }

  @Operation(operationId = "getCpuUsage", summary = "获取CPU使用率数据", description = "获取CPU使用率历史数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/cpu")
  public ApiLocaleResult<CpuUsageVo> getCpuUsage(
      @RequestParam(defaultValue = "1h") String period) {
    return ApiLocaleResult.success(systemMonitoringFacade.getCpuUsage(period));
  }

  @Operation(operationId = "getMemoryUsage", summary = "获取内存使用数据", description = "获取内存使用历史数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/memory")
  public ApiLocaleResult<MemoryUsageVo> getMemoryUsage(
      @RequestParam(defaultValue = "1h") String period) {
    return ApiLocaleResult.success(systemMonitoringFacade.getMemoryUsage(period));
  }

  @Operation(operationId = "getDiskUsage", summary = "获取磁盘使用数据", description = "获取磁盘使用信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/disk")
  public ApiLocaleResult<DiskUsageVo> getDiskUsage() {
    return ApiLocaleResult.success(systemMonitoringFacade.getDiskUsage());
  }

  @Operation(operationId = "getNetworkUsage", summary = "获取网络流量数据", description = "获取网络流量历史数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/network")
  public ApiLocaleResult<NetworkUsageVo> getNetworkUsage(
      @RequestParam(defaultValue = "1h") String period) {
    return ApiLocaleResult.success(systemMonitoringFacade.getNetworkUsage(period));
  }

  @Operation(operationId = "getProcesses", summary = "获取进程列表", description = "获取系统进程列表（仅包含Application编码相关进程和MySQL、Postgres、Nginx、Docker进程）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/processes")
  public ApiLocaleResult<List<ProcessInfoVo>> getProcesses() {
    return ApiLocaleResult.success(systemMonitoringFacade.getProcesses());
  }

}
