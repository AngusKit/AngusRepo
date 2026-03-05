package cloud.xcan.angus.core.repo.application.query.activitylog;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityLog;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.vo.ActivityLogStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.vo.ActivityUserListVo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 活动日志查询接口
 */
public interface ActivityLogQuery {

  /**
   * 查询活动日志列表（分页）
   */
  Page<ActivityLog> find(GenericSpecification<ActivityLog> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  /**
   * 根据ID查询活动日志
   */
  Optional<ActivityLog> findById(String id);

  /**
   * 查询活动日志统计信息
   */
  ActivityLogStatisticsVo getStatistics(LocalDateTime startDate, LocalDateTime endDate);

  /**
   * 获取唯一用户列表
   */
  ActivityUserListVo getUniqueUsers();

  /**
   * 查询用于导出的活动日志列表
   */
  List<ActivityLog> findForExport(GenericSpecification<ActivityLog> spec);
}
