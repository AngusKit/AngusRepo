package cloud.xcan.angus.core.gm.interfaces.interfaces.facade;

import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.ErrorRequestFindDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceStatsFindDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.ErrorRequestDetailVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.ErrorRequestVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceMonitoringOverviewVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceStatsDetailVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceStatsVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.RealtimeQpsVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.RealtimeResponseTimeVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.RealtimeStatusCodeDistributionVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.TopCallsVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.TopErrorsVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.TopSlowVo;
import cloud.xcan.angus.remote.PageResult;
import java.time.LocalDateTime;
import java.util.List;

public interface InterfaceMonitoringFacade {

  /**
   * 获取接口监控概览
   */
  InterfaceMonitoringOverviewVo getOverview();

  /**
   * 获取接口调用统计列表
   */
  PageResult<InterfaceStatsVo> listStats(InterfaceStatsFindDto dto);

  /**
   * 获取单个接口详细统计
   */
  InterfaceStatsDetailVo getStatsDetail(String serviceName, String uri, LocalDateTime startDate,
      LocalDateTime endDate, String period);

  /**
   * 获取错误请求列表
   */
  PageResult<ErrorRequestVo> listErrorRequests(ErrorRequestFindDto dto);

  /**
   * 获取错误请求详情
   */
  ErrorRequestDetailVo getErrorRequestDetail(Long id);

  /**
   * 获取实时QPS数据
   *
   * @param period 时间周期（1h、6h、24h、7d、30d），为空时默认24h
   */
  RealtimeQpsVo getRealtimeQps(String period);

  /**
   * 获取实时响应时间数据
   *
   * @param period 时间周期（1h、6h、24h、7d、30d），为空时默认24h
   */
  RealtimeResponseTimeVo getRealtimeResponseTime(String period);

  /**
   * 获取实时HTTP状态码分布
   *
   * @param period 时间周期（1h、6h、24h、7d、30d），为空时默认24h
   */
  RealtimeStatusCodeDistributionVo getRealtimeStatusCodeDistribution(String period);

  /**
   * 获取调用量TOP接口
   */
  List<TopCallsVo> getTopCalls(Integer limit, String period);

  /**
   * 获取响应时间TOP接口
   */
  List<TopSlowVo> getTopSlow(Integer limit, String period);

  /**
   * 获取错误率TOP接口
   */
  List<TopErrorsVo> getTopErrors(Integer limit, String period);
}
