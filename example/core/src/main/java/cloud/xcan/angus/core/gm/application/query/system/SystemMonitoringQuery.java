package cloud.xcan.angus.core.gm.application.query.system;

import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.CpuUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.DiskUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.EnvironmentVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.HealthCheckVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.MemoryUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.MonitoringOverviewVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.NetworkUsageVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.ProcessInfoVo;
import java.util.List;

/**
 * 系统监控服务接口
 */
public interface SystemMonitoringQuery {

  /**
   * 获取系统环境信息
   */
  EnvironmentVo getEnvironment();

  /**
   * 获取系统监控概览
   */
  MonitoringOverviewVo getOverview();

  /**
   * 获取系统健康检查
   */
  HealthCheckVo getHealth();

  /**
   * 获取CPU使用率数据
   *
   * @param period 时间周期，如：1h, 6h, 24h
   */
  CpuUsageVo getCpuUsage(String period);

  /**
   * 获取内存使用数据
   *
   * @param period 时间周期，如：1h, 6h, 24h
   */
  MemoryUsageVo getMemoryUsage(String period);

  /**
   * 获取磁盘使用数据
   */
  DiskUsageVo getDiskUsage();

  /**
   * 获取网络流量数据
   *
   * @param period 时间周期，如：1h, 6h, 24h
   */
  NetworkUsageVo getNetworkUsage(String period);

  /**
   * 获取进程列表 仅返回包含Application编码的进程和MySQL、Postgres、Nginx、Docker进程
   */
  List<ProcessInfoVo> getProcesses();

}
