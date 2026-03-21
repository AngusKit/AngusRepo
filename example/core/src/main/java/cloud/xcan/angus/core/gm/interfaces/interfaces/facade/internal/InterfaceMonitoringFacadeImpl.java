package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.internal;

import cloud.xcan.angus.core.gm.application.query.interfaces.InterfaceMonitoringQuery;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.InterfaceMonitoringFacade;
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
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class InterfaceMonitoringFacadeImpl implements InterfaceMonitoringFacade {

  @Resource
  private InterfaceMonitoringQuery apiMonitoringQuery;

  @Override
  public InterfaceMonitoringOverviewVo getOverview() {
    return apiMonitoringQuery.getOverview();
  }

  @Override
  public PageResult<InterfaceStatsVo> listStats(InterfaceStatsFindDto dto) {
    return apiMonitoringQuery.listStats(dto);
  }

  @Override
  public InterfaceStatsDetailVo getStatsDetail(String serviceName, String uri,
      LocalDateTime startDate, LocalDateTime endDate, String period) {
    return apiMonitoringQuery.getStatsDetail(serviceName, uri, startDate, endDate, period);
  }

  @Override
  public PageResult<ErrorRequestVo> listErrorRequests(ErrorRequestFindDto dto) {
    return apiMonitoringQuery.listErrorRequests(dto);
  }

  @Override
  public ErrorRequestDetailVo getErrorRequestDetail(Long id) {
    return apiMonitoringQuery.getErrorRequestDetail(id);
  }

  @Override
  public RealtimeQpsVo getRealtimeQps(String period) {
    return apiMonitoringQuery.getRealtimeQps(StringUtils.hasText(period) ? period : "24h");
  }

  @Override
  public RealtimeResponseTimeVo getRealtimeResponseTime(String period) {
    return apiMonitoringQuery.getRealtimeResponseTime(StringUtils.hasText(period) ? period : "24h");
  }

  @Override
  public RealtimeStatusCodeDistributionVo getRealtimeStatusCodeDistribution(String period) {
    return apiMonitoringQuery.getRealtimeStatusCodeDistribution(
        StringUtils.hasText(period) ? period : "24h");
  }

  @Override
  public List<TopCallsVo> getTopCalls(Integer limit, String period) {
    return apiMonitoringQuery.getTopCalls(limit, period);
  }

  @Override
  public List<TopSlowVo> getTopSlow(Integer limit, String period) {
    return apiMonitoringQuery.getTopSlow(limit, period);
  }

  @Override
  public List<TopErrorsVo> getTopErrors(Integer limit, String period) {
    return apiMonitoringQuery.getTopErrors(limit, period);
  }
}
