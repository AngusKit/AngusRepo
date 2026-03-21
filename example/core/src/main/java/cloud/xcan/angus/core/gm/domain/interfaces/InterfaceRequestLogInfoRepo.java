package cloud.xcan.angus.core.gm.domain.interfaces;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

/**
 * API请求日志仓储接口
 */
@NoRepositoryBean
public interface InterfaceRequestLogInfoRepo extends BaseRepository<InterfaceRequestLogInfo, Long> {

  /**
   * 统计各请求方法的数量
   */
  @Query("""
      SELECT a.method, COUNT(a) FROM InterfaceRequestLogInfo a \
      WHERE a.requestDate BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode) \
      GROUP BY a.method""")
  List<Object[]> countByMethodAndDateRange(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 统计各状态码范围的数量
   */
  @Query("""
      SELECT \
      CASE \
        WHEN a.status >= 200 AND a.status < 300 THEN '2xx' \
        WHEN a.status >= 400 AND a.status < 500 THEN '4xx' \
        WHEN a.status >= 500 THEN '5xx' \
        ELSE 'other' \
      END, COUNT(a) \
      FROM InterfaceRequestLogInfo a \
      WHERE a.requestDate BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode) \
      GROUP BY \
      CASE \
        WHEN a.status >= 200 AND a.status < 300 THEN '2xx' \
        WHEN a.status >= 400 AND a.status < 500 THEN '4xx' \
        WHEN a.status >= 500 THEN '5xx' \
        ELSE 'other' \
      END""")
  List<Object[]> countByStatusRangeAndDateRange(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 查询请求最频繁的端点TOP N
   */
  @Query("""
      SELECT a.uri, COUNT(a) as requestCount, AVG(a.elapsedMillis) as avgResponseTime \
      FROM InterfaceRequestLogInfo a \
      WHERE a.requestDate BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode) \
      GROUP BY a.uri \
      ORDER BY requestCount DESC""")
  List<Object[]> findTopEndpointsByRequestCount(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 查询请求最频繁的API密钥TOP N
   */
  @Query("""
      SELECT a.apiKeyId, a.apiKey, COUNT(a) as requestCount \
      FROM InterfaceRequestLogInfo a \
      WHERE a.requestDate BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode) \
      GROUP BY a.apiKeyId, a.apiKey \
      ORDER BY requestCount DESC""")
  List<Object[]> findTopApiKeysByRequestCount(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 统计指定时间范围内的总请求次数
   */
  @Query("""
      SELECT COUNT(a) FROM InterfaceRequestLogInfo a \
      WHERE a.requestDate BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode)""")
  long countByDateRange(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 统计成功请求次数（2xx状态码）
   */
  @Query("""
      SELECT COUNT(a) FROM InterfaceRequestLogInfo a \
      WHERE a.requestDate BETWEEN :startDate AND :endDate \
      AND a.status >= 200 AND a.status < 300 \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode)""")
  long countSuccessByDateRange(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 统计失败请求次数（4xx和5xx状态码）
   */
  @Query("""
      SELECT COUNT(a) FROM InterfaceRequestLogInfo a \
      WHERE a.requestDate BETWEEN :startDate AND :endDate \
      AND (a.status >= 400 AND a.status < 600) \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode)""")
  long countErrorByDateRange(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 计算平均响应时间
   */
  @Query("""
      SELECT AVG(a.elapsedMillis) FROM InterfaceRequestLogInfo a \
      WHERE a.requestDate BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode)""")
  Double avgResponseTimeByDateRange(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 按服务名称、URI和方法分组统计接口调用
   */
  @Query("""
      SELECT a.serviceName, a.uri, a.method, \
      COUNT(a) as calls, \
      AVG(a.elapsedMillis) as avgResponseTime, \
      SUM(CASE WHEN a.status >= 400 THEN 1 ELSE 0 END) as errorCount \
      FROM InterfaceRequestLogInfo a \
      WHERE a.requestDate BETWEEN :startDate AND :endDate \
      AND (:serviceName IS NULL OR a.serviceName = :serviceName) \
      AND (:uri IS NULL OR a.uri LIKE CONCAT('%', :uri, '%')) \
      AND (:method IS NULL OR a.method = :method) \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode) \
      GROUP BY a.serviceName, a.uri, a.method""")
  List<Object[]> statsByServiceAndUriAndMethod(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("serviceName") String serviceName,
      @Param("uri") String uri,
      @Param("method") String method,
      @Param("applicationCode") String applicationCode);

  /**
   * 统计指定接口的详细数据（按小时分组）
   */
  @Query(value = """
      SELECT DATE_FORMAT(request_date, '%Y-%m-%d %H:00:00') as time, \
      COUNT(*) as calls, \
      AVG(elapsed_millis) as avgResponseTime, \
      SUM(CASE WHEN status >= 400 THEN 1 ELSE 0 END) as errorCount \
      FROM gm_interface_request_log \
      WHERE service_name = :serviceName AND uri = :uri \
      AND request_date BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR application_code = :applicationCode) \
      GROUP BY DATE_FORMAT(request_date, '%Y-%m-%d %H:00:00') \
      ORDER BY time""", nativeQuery = true)
  List<Object[]> statsDetailByTime(
      @Param("serviceName") String serviceName,
      @Param("uri") String uri,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 统计响应时间分布
   */
  @Query("""
      SELECT \
      CASE \
        WHEN a.elapsedMillis < 100 THEN '<100ms' \
        WHEN a.elapsedMillis < 500 THEN '100-500ms' \
        WHEN a.elapsedMillis < 1000 THEN '500ms-1s' \
        WHEN a.elapsedMillis < 3000 THEN '1s-3s' \
        ELSE '>3s' \
      END, COUNT(a) \
      FROM InterfaceRequestLogInfo a \
      WHERE a.serviceName = :serviceName AND a.uri = :uri \
      AND a.requestDate BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode) \
      GROUP BY \
      CASE \
        WHEN a.elapsedMillis < 100 THEN '<100ms' \
        WHEN a.elapsedMillis < 500 THEN '100-500ms' \
        WHEN a.elapsedMillis < 1000 THEN '500ms-1s' \
        WHEN a.elapsedMillis < 3000 THEN '1s-3s' \
        ELSE '>3s' \
      END""")
  List<Object[]> statsResponseTimeDistribution(
      @Param("serviceName") String serviceName,
      @Param("uri") String uri,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 统计状态码分布
   */
  @Query("""
      SELECT \
      CASE \
        WHEN a.status >= 200 AND a.status < 300 THEN '2xx' \
        WHEN a.status >= 300 AND a.status < 400 THEN '3xx' \
        WHEN a.status >= 400 AND a.status < 500 THEN '4xx' \
        WHEN a.status >= 500 THEN '5xx' \
        ELSE 'other' \
      END, COUNT(a) \
      FROM InterfaceRequestLogInfo a \
      WHERE a.serviceName = :serviceName AND a.uri = :uri \
      AND a.requestDate BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode) \
      GROUP BY \
      CASE \
        WHEN a.status >= 200 AND a.status < 300 THEN '2xx' \
        WHEN a.status >= 300 AND a.status < 400 THEN '3xx' \
        WHEN a.status >= 400 AND a.status < 500 THEN '4xx' \
        WHEN a.status >= 500 THEN '5xx' \
        ELSE 'other' \
      END""")
  List<Object[]> statsStatusCodeDistribution(
      @Param("serviceName") String serviceName,
      @Param("uri") String uri,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 查询调用量TOP接口
   */
  @Query("""
      SELECT a.serviceName, a.uri, a.method, \
      COUNT(a) as calls, \
      CAST(SUM(CASE WHEN a.status >= 400 THEN 1 ELSE 0 END) AS DOUBLE) / COUNT(a) * 100 as errorRate \
      FROM InterfaceRequestLogInfo a \
      WHERE a.requestDate BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode) \
      GROUP BY a.serviceName, a.uri, a.method \
      ORDER BY calls DESC""")
  List<Object[]> findTopCalls(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 查询响应时间TOP接口
   */
  @Query("""
      SELECT a.serviceName, a.uri, a.method, \
      AVG(a.elapsedMillis) as avgResponseTime, \
      COUNT(a) as calls \
      FROM InterfaceRequestLogInfo a \
      WHERE a.requestDate BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode) \
      GROUP BY a.serviceName, a.uri, a.method \
      HAVING COUNT(a) >= 10 \
      ORDER BY avgResponseTime DESC""")
  List<Object[]> findTopSlow(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 查询错误率TOP接口
   */
  @Query("""
      SELECT a.serviceName, a.uri, a.method, \
      CAST(SUM(CASE WHEN a.status >= 400 THEN 1 ELSE 0 END) AS DOUBLE) / COUNT(a) * 100 as errorRate, \
      COUNT(a) as calls, \
      SUM(CASE WHEN a.status >= 400 THEN 1 ELSE 0 END) as failedCalls \
      FROM InterfaceRequestLogInfo a \
      WHERE a.requestDate BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode) \
      GROUP BY a.serviceName, a.uri, a.method \
      HAVING COUNT(a) >= 10 AND SUM(CASE WHEN a.status >= 400 THEN 1 ELSE 0 END) > 0 \
      ORDER BY errorRate DESC""")
  List<Object[]> findTopErrors(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 统计慢请求数量（超过指定阈值）
   */
  @Query("""
      SELECT COUNT(a) FROM InterfaceRequestLogInfo a \
      WHERE a.requestDate BETWEEN :startDate AND :endDate \
      AND a.elapsedMillis >= :minDuration \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode)""")
  long countSlowRequests(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("minDuration") Long minDuration,
      @Param("applicationCode") String applicationCode);

  /**
   * 计算当前QPS（最近1分钟）
   */
  @Query("""
      SELECT COUNT(a) FROM InterfaceRequestLogInfo a \
      WHERE a.requestDate >= :startDate \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode)""")
  long countRecentRequests(
      @Param("startDate") LocalDateTime startDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 获取QPS时间线数据（按分钟分组）
   */
  @Query(value = """
      SELECT DATE_FORMAT(request_date, '%Y-%m-%d %H:%i:00') as time, \
      COUNT(*) as qps \
      FROM gm_interface_request_log \
      WHERE request_date BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR application_code = :applicationCode) \
      GROUP BY DATE_FORMAT(request_date, '%Y-%m-%d %H:%i:00') \
      ORDER BY time""", nativeQuery = true)
  List<Object[]> qpsTimeline(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 获取响应时间时间线数据（按分钟分组）
   */
  @Query(value = """
      SELECT DATE_FORMAT(request_date, '%Y-%m-%d %H:%i:00') as time, \
      AVG(elapsed_millis) as avg \
      FROM gm_interface_request_log \
      WHERE request_date BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR application_code = :applicationCode) \
      GROUP BY DATE_FORMAT(request_date, '%Y-%m-%d %H:%i:00') \
      ORDER BY time""", nativeQuery = true)
  List<Object[]> responseTimeTimeline(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 获取原始响应时间数据（按时间桶和耗时），用于计算百分位数 限制返回10万条以避免内存溢出
   */
  @Query(value = """
      SELECT DATE_FORMAT(request_date, '%Y-%m-%d %H:%i:00'), elapsed_millis \
      FROM gm_interface_request_log \
      WHERE request_date BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR application_code = :applicationCode) \
      ORDER BY request_date \
      LIMIT 100000""", nativeQuery = true)
  List<Object[]> findResponseTimeRawByDateRange(
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 按天统计指定接口的请求数
   */
  @Query(value = """
      SELECT DATE(request_date) as date, COUNT(*) as count \
      FROM gm_interface_request_log \
      WHERE service_name = :serviceName AND uri = :uri AND method = :method \
      AND request_date BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR application_code = :applicationCode) \
      GROUP BY DATE(request_date) \
      ORDER BY date""", nativeQuery = true)
  List<Object[]> countDailyByInterface(
      @Param("serviceName") String serviceName,
      @Param("uri") String uri,
      @Param("method") String method,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 统计指定接口的错误码分布
   */
  @Query("""
      SELECT a.status, COUNT(a) \
      FROM InterfaceRequestLogInfo a \
      WHERE a.serviceName = :serviceName AND a.uri = :uri AND a.method = :method \
      AND a.requestDate BETWEEN :startDate AND :endDate \
      AND a.status >= 400 \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode) \
      GROUP BY a.status \
      ORDER BY COUNT(a) DESC""")
  List<Object[]> countErrorCodesByInterface(
      @Param("serviceName") String serviceName,
      @Param("uri") String uri,
      @Param("method") String method,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 统计指定接口的总请求数
   */
  @Query("""
      SELECT COUNT(a) FROM InterfaceRequestLogInfo a \
      WHERE a.serviceName = :serviceName AND a.uri = :uri AND a.method = :method \
      AND a.requestDate BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode)""")
  long countByInterface(
      @Param("serviceName") String serviceName,
      @Param("uri") String uri,
      @Param("method") String method,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 统计指定接口的成功请求数
   */
  @Query("""
      SELECT COUNT(a) FROM InterfaceRequestLogInfo a \
      WHERE a.serviceName = :serviceName AND a.uri = :uri AND a.method = :method \
      AND a.requestDate BETWEEN :startDate AND :endDate \
      AND a.status >= 200 AND a.status < 300 \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode)""")
  long countSuccessByInterface(
      @Param("serviceName") String serviceName,
      @Param("uri") String uri,
      @Param("method") String method,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 统计指定接口的失败请求数
   */
  @Query("""
      SELECT COUNT(a) FROM InterfaceRequestLogInfo a \
      WHERE a.serviceName = :serviceName AND a.uri = :uri AND a.method = :method \
      AND a.requestDate BETWEEN :startDate AND :endDate \
      AND (a.status >= 400 AND a.status < 600) \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode)""")
  long countFailedByInterface(
      @Param("serviceName") String serviceName,
      @Param("uri") String uri,
      @Param("method") String method,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 计算指定接口的平均响应时间
   */
  @Query("""
      SELECT AVG(a.elapsedMillis) FROM InterfaceRequestLogInfo a \
      WHERE a.serviceName = :serviceName AND a.uri = :uri AND a.method = :method \
      AND a.requestDate BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode)""")
  Double avgResponseTimeByInterface(
      @Param("serviceName") String serviceName,
      @Param("uri") String uri,
      @Param("method") String method,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 获取指定接口的最大响应时间
   */
  @Query("""
      SELECT MAX(a.elapsedMillis) FROM InterfaceRequestLogInfo a \
      WHERE a.serviceName = :serviceName AND a.uri = :uri AND a.method = :method \
      AND a.requestDate BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode)""")
  Long maxResponseTimeByInterface(
      @Param("serviceName") String serviceName,
      @Param("uri") String uri,
      @Param("method") String method,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

  /**
   * 获取指定接口的最小响应时间
   */
  @Query("""
      SELECT MIN(a.elapsedMillis) FROM InterfaceRequestLogInfo a \
      WHERE a.serviceName = :serviceName AND a.uri = :uri AND a.method = :method \
      AND a.requestDate BETWEEN :startDate AND :endDate \
      AND (:applicationCode IS NULL OR a.applicationCode = :applicationCode)""")
  Long minResponseTimeByInterface(
      @Param("serviceName") String serviceName,
      @Param("uri") String uri,
      @Param("method") String method,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("applicationCode") String applicationCode);

}
