package cloud.xcan.angus.core.gm.domain.authorization;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AuthorizationRepo extends BaseRepository<Authorization, Long> {

  /**
   * 根据主体类型和主体ID查找授权
   */
  Optional<Authorization> findBySubjectTypeAndSubjectId(AuthorizationSubjectType subjectType,
      Long subjectId);

  /**
   * 根据状态分页查找授权
   */
  Page<Authorization> findByStatus(EnabledStatus status, Pageable pageable);

  /**
   * 根据主体类型和主体ID判断授权是否存在
   */
  boolean existsBySubjectTypeAndSubjectId(AuthorizationSubjectType subjectType, Long subjectId);

  /**
   * 根据状态统计授权数量
   */
  long countByStatus(EnabledStatus status);

  /**
   * 根据主体类型统计授权数量
   */
  long countBySubjectType(AuthorizationSubjectType subjectType);

  /**
   * 按主体类型分组统计授权数量
   */
  @Query(value = "SELECT subject_type, COUNT(*) as count FROM gm_authorization GROUP BY subject_type", nativeQuery = true)
  List<Object[]> countGroupBySubjectType();

  /**
   * 根据主体ID列表和状态查找授权ID列表
   */
  @Query("SELECT a.id FROM Authorization a WHERE a.subjectId IN ?1 AND a.status = ?2")
  List<Long> findAuthorizationIdsBySubjectIdInAndStatus(Set<Long> subjectIds, EnabledStatus status);

  /**
   * 根据租户ID删除所有授权
   */
  @Modifying
  void deleteByTenantId(Long tenantId);

  /**
   * 根据主体类型和主体ID查找授权ID列表（用于批量删除前获取关联的 authorization_role）
   */
  @Query("SELECT a.id FROM Authorization a WHERE a.subjectType = ?1 AND a.subjectId = ?2")
  List<Long> findIdsBySubjectTypeAndSubjectId(AuthorizationSubjectType subjectType, Long subjectId);

  /**
   * 根据主体类型和主体ID批量删除授权（幂等，影响 0 或 1 行均不抛错，避免 StaleStateException）
   */
  @Modifying
  @Query("DELETE FROM Authorization a WHERE a.subjectType = ?1 AND a.subjectId = ?2")
  void deleteBySubjectTypeAndSubjectId(AuthorizationSubjectType subjectType, Long subjectId);

  /**
   * 根据ID删除授权（幂等，影响 0 行不抛错，避免记录已被级联删除时触发 StaleStateException）
   */
  @Modifying
  @Query("DELETE FROM Authorization a WHERE a.id = ?1")
  int deleteByIdIfExists(Long id);

  /**
   * 根据ID列表批量删除授权（幂等，避免记录已被级联删除时触发 StaleStateException）
   */
  @Modifying
  @Query("DELETE FROM Authorization a WHERE a.id IN ?1")
  int deleteByIdIn(List<Long> ids);

}
