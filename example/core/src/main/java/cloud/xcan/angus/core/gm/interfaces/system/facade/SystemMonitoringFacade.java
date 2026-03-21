package cloud.xcan.angus.core.gm.interfaces.system.facade;

import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.CpuUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.DiskUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.EnvironmentVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.HealthCheckVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.MemoryUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.MonitoringOverviewVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.NetworkUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.ProcessInfoVo;
import java.util.List;

public interface SystemMonitoringFacade {

  EnvironmentVo getEnvironment();

  MonitoringOverviewVo getOverview();

  HealthCheckVo getHealth();

  CpuUsageVo getCpuUsage(String period);

  MemoryUsageVo getMemoryUsage(String period);

  DiskUsageVo getDiskUsage();

  NetworkUsageVo getNetworkUsage(String period);

  List<ProcessInfoVo> getProcesses();

}
