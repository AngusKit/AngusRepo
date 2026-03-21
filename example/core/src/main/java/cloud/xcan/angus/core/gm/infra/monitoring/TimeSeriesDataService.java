package cloud.xcan.angus.core.gm.infra.monitoring;

import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.CpuUsageVo.CpuHistory;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.MemoryUsageVo.MemoryHistory;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.NetworkUsageVo.NetworkHistory;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 时序数据服务接口
 * <p>
 * 用于存储和获取系统监控的时序数据。 当前实现为内存存储，后续可通过定时任务按时间顺序写入数据库。
 * </p>
 */
public interface TimeSeriesDataService {

  /**
   * 获取CPU使用率历史数据
   *
   * @param hours 查询最近N小时的数据
   */
  List<CpuHistory> getCpuHistory(int hours);

  /**
   * 获取内存使用历史数据
   *
   * @param hours 查询最近N小时的数据
   */
  List<MemoryHistory> getMemoryHistory(int hours);

  /**
   * 获取网络流量历史数据
   *
   * @param hours 查询最近N小时的数据
   */
  List<NetworkHistory> getNetworkHistory(int hours);

  /**
   * 添加CPU使用率数据点
   */
  void addCpuData(LocalDateTime time, double usage);

  /**
   * 添加内存使用数据点
   */
  void addMemoryData(LocalDateTime time, String used, double usagePercent);

  /**
   * 添加网络流量数据点
   */
  void addNetworkData(LocalDateTime time, String inRate, String outRate);
}
