package cloud.xcan.angus.core.gm.application.query.user.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.user.LoginHistoryQuery;
import cloud.xcan.angus.core.gm.domain.user.LoginHistory;
import cloud.xcan.angus.core.gm.domain.user.LoginHistoryRepo;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 用户登录历史记录查询服务实现
 */
@Service
public class LoginHistoryQueryImpl implements LoginHistoryQuery {

  @Resource
  private LoginHistoryRepo loginHistoryRepo;

  @Override
  public Page<LoginHistory> findByUserId(Long userId, Pageable pageable) {
    return new BizTemplate<Page<LoginHistory>>() {
      @Override
      protected Page<LoginHistory> process() {
        return loginHistoryRepo.findByUserIdOrderByLoginTimeDesc(userId, pageable);
      }
    }.execute();
  }

  @Override
  public Map<Long, LocalDateTime> findLastLoginByUserIds(List<Long> userIds) {
    return new BizTemplate<Map<Long, LocalDateTime>>() {
      @Override
      protected Map<Long, LocalDateTime> process() {
        if (userIds == null || userIds.isEmpty()) {
          return new HashMap<>();
        }

        // 批量查询最后登录时间
        List<Object[]> results = loginHistoryRepo.findLastLoginByUserIds(userIds);
        Map<Long, LocalDateTime> lastLoginMap = new HashMap<>();

        for (Object[] result : results) {
          Long userId = ((Number) result[0]).longValue();
          LocalDateTime lastLoginTime = (LocalDateTime) result[1];
          lastLoginMap.put(userId, lastLoginTime);
        }

        return lastLoginMap;
      }
    }.execute();
  }

}
