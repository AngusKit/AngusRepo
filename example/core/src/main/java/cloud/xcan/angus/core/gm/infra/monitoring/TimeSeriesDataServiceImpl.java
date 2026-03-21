package cloud.xcan.angus.core.gm.infra.monitoring;

import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.CpuUsageVo.CpuHistory;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.MemoryUsageVo.MemoryHistory;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.NetworkUsageVo.NetworkHistory;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 时序数据服务实现（内存存储）
 * <p>
 * 使用内存队列存储时序数据，后续可通过定时任务将数据写入数据库。
 * </p>
 */
@Service
public class TimeSeriesDataServiceImpl implements TimeSeriesDataService {

  // CPU使用率数据队列（最多保留1000条）
  private final ConcurrentLinkedQueue<CpuHistory> cpuDataQueue = new ConcurrentLinkedQueue<>();

  // 内存使用数据队列（最多保留1000条）
  private final ConcurrentLinkedQueue<MemoryHistory> memoryDataQueue = new ConcurrentLinkedQueue<>();

  // 网络流量数据队列（最多保留1000条）
  private final ConcurrentLinkedQueue<NetworkHistory> networkDataQueue = new ConcurrentLinkedQueue<>();

  // 只保留7天数据
  private static final int MAX_DATA_SIZE = 7 * 24 * 60 * (60 / 15);

  @Override
  public List<CpuHistory> getCpuHistory(int hours) {
    LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hours);

    List<CpuHistory> result = cpuDataQueue.stream()
        .filter(data -> data.getTime().isAfter(cutoffTime))
        .sorted((a, b) -> a.getTime().compareTo(b.getTime()))
        .collect(Collectors.toList());

    // 如果没有数据，返回空列表
    return result.isEmpty() ? Collections.emptyList() : result;
  }

  @Override
  public List<MemoryHistory> getMemoryHistory(int hours) {
    LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hours);

    List<MemoryHistory> result = memoryDataQueue.stream()
        .filter(data -> data.getTime().isAfter(cutoffTime))
        .sorted((a, b) -> a.getTime().compareTo(b.getTime()))
        .collect(Collectors.toList());

    return result.isEmpty() ? Collections.emptyList() : result;
  }

  @Override
  public List<NetworkHistory> getNetworkHistory(int hours) {
    LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hours);

    List<NetworkHistory> result = networkDataQueue.stream()
        .filter(data -> data.getTime().isAfter(cutoffTime))
        .sorted((a, b) -> a.getTime().compareTo(b.getTime()))
        .collect(Collectors.toList());

    return result.isEmpty() ? Collections.emptyList() : result;
  }

  @Override
  public void addCpuData(LocalDateTime time, double usage) {
    CpuHistory history = new CpuHistory();
    history.setTime(time);
    history.setUsage(usage);

    cpuDataQueue.offer(history);

    // 限制队列大小
    while (cpuDataQueue.size() > MAX_DATA_SIZE) {
      cpuDataQueue.poll();
    }
  }

  @Override
  public void addMemoryData(LocalDateTime time, String used, double usagePercent) {
    MemoryHistory history = new MemoryHistory();
    history.setTime(time);
    history.setUsed(used);
    history.setUsagePercent(usagePercent);

    memoryDataQueue.offer(history);

    // 限制队列大小
    while (memoryDataQueue.size() > MAX_DATA_SIZE) {
      memoryDataQueue.poll();
    }
  }

  @Override
  public void addNetworkData(LocalDateTime time, String inRate, String outRate) {
    NetworkHistory history = new NetworkHistory();
    history.setTime(time);
    history.setInRate(inRate);
    history.setOutRate(outRate);

    networkDataQueue.offer(history);

    // 限制队列大小
    while (networkDataQueue.size() > MAX_DATA_SIZE) {
      networkDataQueue.poll();
    }
  }
}
