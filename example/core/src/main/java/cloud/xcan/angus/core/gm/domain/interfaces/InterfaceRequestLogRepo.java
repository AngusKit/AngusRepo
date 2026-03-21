package cloud.xcan.angus.core.gm.domain.interfaces;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

/**
 * API请求日志仓储接口
 */
@NoRepositoryBean
public interface InterfaceRequestLogRepo extends BaseRepository<InterfaceRequestLog, Long> {

  /**
   * 删除指定时间之前的日志
   */
  @Modifying
  @Query("""
      DELETE FROM InterfaceRequestLog a \
      WHERE a.requestDate < :beforeDate \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode)""")
  int deleteByCreatedDateBefore(
      @Param("beforeDate") LocalDateTime beforeDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 查询指定接口最后一次错误调用的记录（按请求时间倒序取第一条）
   */
  @Query("""
      SELECT a FROM InterfaceRequestLog a \
      WHERE a.serviceName = :serviceName AND a.uri = :uri AND a.method = :method \
      AND a.status >= 400 \
      AND a.requestDate BETWEEN :startDate AND :endDate \
      ORDER BY a.requestDate DESC""")
  List<InterfaceRequestLog> findLastErrorByInterface(
      @Param("serviceName") String serviceName,
      @Param("uri") String uri,
      @Param("method") String method,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      Pageable pageable);
}
