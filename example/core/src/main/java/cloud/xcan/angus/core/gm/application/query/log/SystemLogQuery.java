package cloud.xcan.angus.core.gm.application.query.log;

import cloud.xcan.angus.core.gm.domain.log.SystemLog;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.SystemLogContentDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.SystemLogStatisticsDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.SystemLogContentVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.SystemLogStatisticsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 系统日志查询服务接口
 */
public interface SystemLogQuery {

  /**
   * 查询日志详情
   */
  SystemLog findAndCheck(Long id);

  /**
   * 分页查询日志列表
   */
  Page<SystemLog> find(GenericSpecification<SystemLog> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  /**
   * 获取日志文件内容
   */
  SystemLogContentVo getContent(Long id, SystemLogContentDto dto);

  /**
   * 获取统计数据
   */
  SystemLogStatisticsVo getStatistics(SystemLogStatisticsDto dto);
}
