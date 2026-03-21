package cloud.xcan.angus.core.gm.application.query.interfaces;

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

public interface InterfaceMonitoringQuery {

  InterfaceMonitoringOverviewVo getOverview();

  PageResult<InterfaceStatsVo> listStats(InterfaceStatsFindDto dto);

  InterfaceStatsDetailVo getStatsDetail(String serviceName, String uri, LocalDateTime startDate,
      LocalDateTime endDate, String period);

  PageResult<ErrorRequestVo> listErrorRequests(ErrorRequestFindDto dto);

  ErrorRequestDetailVo getErrorRequestDetail(Long id);

  RealtimeQpsVo getRealtimeQps(String period);

  RealtimeResponseTimeVo getRealtimeResponseTime(String period);

  RealtimeStatusCodeDistributionVo getRealtimeStatusCodeDistribution(String period);

  List<TopCallsVo> getTopCalls(Integer limit, String period);

  List<TopSlowVo> getTopSlow(Integer limit, String period);

  List<TopErrorsVo> getTopErrors(Integer limit, String period);
}
