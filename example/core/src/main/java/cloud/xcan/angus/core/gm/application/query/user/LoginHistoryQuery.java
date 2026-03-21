package cloud.xcan.angus.core.gm.application.query.user;

import cloud.xcan.angus.core.gm.domain.user.LoginHistory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 用户登录历史记录查询服务接口
 */
public interface LoginHistoryQuery {

  /**
   * 根据用户ID分页查询登录历史记录
   */
  Page<LoginHistory> findByUserId(Long userId, Pageable pageable);

  /**
   * 根据用户ID列表批量查询最后登录时间
   *
   * @param userIds 用户ID列表
   * @return Map<用户ID, 最后登录时间>
   */
  Map<Long, LocalDateTime> findLastLoginByUserIds(List<Long> userIds);

}
