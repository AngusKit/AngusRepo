package cloud.xcan.angus.core.gm.domain.user;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

/**
 * 用户登录历史记录仓储接口
 */
@NoRepositoryBean
public interface LoginHistoryRepo extends BaseRepository<LoginHistory, Long> {

  /**
   * 根据用户ID分页查询，按登录时间倒序
   */
  Page<LoginHistory> findByUserIdOrderByLoginTimeDesc(Long userId, Pageable pageable);

  /**
   * 根据用户ID删除
   */
  @Modifying
  void deleteByUserId(Long userId);

  /**
   * 根据用户ID查询最新的登录记录（按登录时间倒序，取第一条）
   */
  LoginHistory findTop1ByUserIdOrderByLoginTimeDesc(Long userId);

  /**
   * 根据用户ID列表批量查询最后登录时间
   */
  @Query("SELECT lh.userId, MAX(lh.loginTime) FROM LoginHistory lh WHERE lh.userId IN :userIds GROUP BY lh.userId")
  List<Object[]> findLastLoginByUserIds(@Param("userIds") List<Long> userIds);

  /**
   * 统计指定时间后有过登录记录的不同用户数量（用于计算活跃率）
   */
  @Query("SELECT COUNT(DISTINCT lh.userId) FROM LoginHistory lh WHERE lh.loginTime >= :since")
  long countDistinctUserIdByLoginTimeAfter(@Param("since") LocalDateTime since);

  /**
   * 统计指定时间后、指定用户ID范围内有过登录记录的不同用户数量
   */
  @Query("SELECT COUNT(DISTINCT lh.userId) FROM LoginHistory lh WHERE lh.loginTime >= :since AND lh.userId IN :userIds")
  long countDistinctUserIdByLoginTimeAfterAndUserIdIn(@Param("since") LocalDateTime since,
      @Param("userIds") Collection<Long> userIds);
}
