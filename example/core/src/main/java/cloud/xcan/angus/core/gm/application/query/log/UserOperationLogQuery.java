package cloud.xcan.angus.core.gm.application.query.log;

import cloud.xcan.angus.core.gm.domain.log.UserOperationLog;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.UserOperationLogStatisticsDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.UserOperationLogStatisticsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 用户操作日志查询服务接口
 */
public interface UserOperationLogQuery {

  /**
   * 查询日志详情
   */
  UserOperationLog findAndCheck(Long id);

  /**
   * 分页查询日志列表
   */
  Page<UserOperationLog> find(GenericSpecification<UserOperationLog> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  /**
   * 获取统计数据
   */
  UserOperationLogStatisticsVo getStatistics(UserOperationLogStatisticsDto dto);
}
