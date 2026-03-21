package cloud.xcan.angus.core.gm.domain.log;

import cloud.xcan.angus.core.gm.domain.log.enums.LogStatus;
import cloud.xcan.angus.core.gm.domain.log.enums.LogType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

/**
 * 系统日志仓储接口
 */
@NoRepositoryBean
public interface SystemLogRepo extends BaseRepository<SystemLog, Long> {

  /**
   * 根据日志类型查询
   */
  List<SystemLog> findByType(LogType type);

  /**
   * 根据状态查询
   */
  List<SystemLog> findByStatus(LogStatus status);

  /**
   * 根据应用ID查询
   */
  List<SystemLog> findByApplicationId(Long applicationId);

  /**
   * 根据日期范围查询
   */
  List<SystemLog> findByDateBetween(LocalDate startDate, LocalDate endDate);

  /**
   * 统计各类型的数量和大小
   */
  @Query("SELECT s.type, COUNT(s), SUM(s.size) FROM SystemLog s " +
      "WHERE s.date BETWEEN :startDate AND :endDate " +
      "AND (:applicationId IS NULL OR s.applicationId = :applicationId) " +
      "GROUP BY s.type")
  List<Object[]> countAndSizeByTypeAndDateRange(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("applicationId") Long applicationId);

  /**
   * 统计各状态的数量
   */
  @Query("SELECT s.status, COUNT(s) FROM SystemLog s " +
      "WHERE s.date BETWEEN :startDate AND :endDate " +
      "AND (:applicationId IS NULL OR s.applicationId = :applicationId) " +
      "GROUP BY s.status")
  List<Object[]> countByStatusAndDateRange(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("applicationId") Long applicationId);

  /**
   * 统计各应用的数量和大小
   */
  @Query("SELECT s.applicationId, COUNT(s), SUM(s.size) FROM SystemLog s " +
      "WHERE s.date BETWEEN :startDate AND :endDate " +
      "AND (:applicationId IS NULL OR s.applicationId = :applicationId) " +
      "GROUP BY s.applicationId " +
      "ORDER BY SUM(s.size) DESC")
  List<Object[]> countAndSizeByApplicationAndDateRange(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("applicationId") Long applicationId);

  /**
   * 查询最早的日志
   */
  @Query("SELECT s FROM SystemLog s " +
      "WHERE s.date BETWEEN :startDate AND :endDate " +
      "AND (:applicationId IS NULL OR s.applicationId = :applicationId) " +
      "ORDER BY s.date ASC, s.createdDate ASC")
  List<SystemLog> findOldestLog(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("applicationId") Long applicationId);

  /**
   * 查询最新的日志
   */
  @Query("SELECT s FROM SystemLog s " +
      "WHERE s.date BETWEEN :startDate AND :endDate " +
      "AND (:applicationId IS NULL OR s.applicationId = :applicationId) " +
      "ORDER BY s.date DESC, s.createdDate DESC")
  List<SystemLog> findNewestLog(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate,
      @Param("applicationId") Long applicationId);

  /**
   * 查询指定日期之前的归档日志
   */
  @Query("SELECT s FROM SystemLog s " +
      "WHERE s.date < :beforeDate " +
      "AND s.status = :status " +
      "AND (:applicationId IS NULL OR s.applicationId = :applicationId)")
  List<SystemLog> findByDateBeforeAndStatus(
      @Param("beforeDate") LocalDate beforeDate,
      @Param("status") LogStatus status,
      @Param("applicationId") Long applicationId);

  /**
   * 删除指定日期之前的归档日志
   */
  @Modifying
  @Query("DELETE FROM SystemLog s " +
      "WHERE s.date < :beforeDate " +
      "AND s.status = :status " +
      "AND (:applicationId IS NULL OR s.applicationId = :applicationId)")
  int deleteByDateBeforeAndStatus(
      @Param("beforeDate") LocalDate beforeDate,
      @Param("status") LogStatus status,
      @Param("applicationId") Long applicationId);
}
