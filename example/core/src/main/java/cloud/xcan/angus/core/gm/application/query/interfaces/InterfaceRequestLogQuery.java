package cloud.xcan.angus.core.gm.application.query.interfaces;

import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLog;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLogInfo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceCallStatsDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceRequestLogStatisticsDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceCallStatsVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.InterfaceRequestLogStatisticsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * API请求日志查询服务接口
 */
public interface InterfaceRequestLogQuery {

  /**
   * 查询日志详情
   */
  InterfaceRequestLog findAndCheck(Long id);

  /**
   * 分页查询日志列表
   */
  Page<InterfaceRequestLogInfo> find(GenericSpecification<InterfaceRequestLogInfo> spec,
      PageRequest pageable, boolean fullTextSearch, String[] match);

  /**
   * 获取统计数据
   */
  InterfaceRequestLogStatisticsVo getStatistics(InterfaceRequestLogStatisticsDto dto);

  /**
   * 获取接口调用统计
   *
   * @param serviceName 服务名称
   * @param uri         接口路径
   * @param method      请求方法
   * @param dto         统计查询参数
   * @return 接口调用统计结果
   */
  InterfaceCallStatsVo getCallStats(String serviceName, String uri, String method,
      InterfaceCallStatsDto dto);
}
