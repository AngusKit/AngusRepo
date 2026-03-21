package cloud.xcan.angus.core.gm.interfaces.system.facade.internal;

import cloud.xcan.angus.core.gm.application.query.system.SystemMonitoringQuery;
import cloud.xcan.angus.core.gm.interfaces.system.facade.SystemMonitoringFacade;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.CpuUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.DiskUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.EnvironmentVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.HealthCheckVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.MemoryUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.MonitoringOverviewVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.NetworkUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.ProcessInfoVo;
import jakarta.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class SystemMonitoringFacadeImpl implements SystemMonitoringFacade {

  @Resource
  private SystemMonitoringQuery systemMonitoringQuery;

  @Override
  public EnvironmentVo getEnvironment() {
    return systemMonitoringQuery.getEnvironment();
  }

  @Override
  public MonitoringOverviewVo getOverview() {
    return systemMonitoringQuery.getOverview();
  }

  @Override
  public HealthCheckVo getHealth() {
    return systemMonitoringQuery.getHealth();
  }

  @Override
  public CpuUsageVo getCpuUsage(String period) {
    return systemMonitoringQuery.getCpuUsage(period);
  }

  @Override
  public MemoryUsageVo getMemoryUsage(String period) {
    return systemMonitoringQuery.getMemoryUsage(period);
  }

  @Override
  public DiskUsageVo getDiskUsage() {
    return systemMonitoringQuery.getDiskUsage();
  }

  @Override
  public NetworkUsageVo getNetworkUsage(String period) {
    return systemMonitoringQuery.getNetworkUsage(period);
  }

  @Override
  public List<ProcessInfoVo> getProcesses() {
    List<ProcessInfoVo> allProcesses = systemMonitoringQuery.getProcesses();
    return allProcesses.stream()
        .sorted(Comparator.comparing(ProcessInfoVo::getCpuPercent).reversed())
        .collect(Collectors.toList());
  }

}
