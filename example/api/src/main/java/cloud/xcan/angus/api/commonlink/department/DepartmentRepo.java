package cloud.xcan.angus.api.commonlink.department;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 部门仓储接口
 */
@Repository("commonDepartmentRepo")
public interface DepartmentRepo extends NameJoinRepository<Department, Long>,
    BaseRepository<Department, Long> {

  /**
   * 检查编码是否存在
   */
  boolean existsByCode(String code);

  /**
   * 检查编码是否存在（排除指定ID）
   */
  boolean existsByCodeAndIdNot(String code, Long id);

  /**
   * 检查名称是否存在
   */
  boolean existsByName(String name);

  /**
   * 检查名称是否存在（排除指定ID）
   */
  boolean existsByNameAndIdNot(String name, Long id);

  /**
   * 根据父部门ID查找子部门列表
   */
  List<Department> findByParentId(Long parentId);

  /**
   * 根据父部门ID和状态查找子部门列表
   */
  List<Department> findByParentIdAndStatus(Long parentId, EnabledStatus status);

  /**
   * 统计指定状态的部门数量
   */
  long countByStatus(EnabledStatus status);

  /**
   * 统计指定父部门下的子部门数量
   */
  long countByParentId(Long parentId);

  /**
   * 查找所有顶级部门（父部门为空的部门）
   */
  List<Department> findByParentIdIsNull();

  /**
   * 查找所有顶级部门（父部门为空且指定状态）
   */
  List<Department> findByParentIdIsNullAndStatus(EnabledStatus status);

  /**
   * 统计指定日期之后创建的部门数量
   */
  long countByCreatedDateAfter(LocalDateTime date);

  /**
   * 统计指定日期之前创建的部门数量（用于计算变化量）
   */
  long countByCreatedDateBefore(LocalDateTime date);

  /**
   * 统计指定层级的部门数量
   */
  long countByLevel(Integer level);

  /**
   * 统计租户下的部门数量
   */
  Long countByTenantId(Long tenantId);

  /**
   * 统计顶级部门数量（父部门为空的部门）
   */
  long countByParentIdIsNull();

  /**
   * 查找部门的最大层级
   */
  @Query("SELECT MAX(d.level) FROM Department d")
  Integer findMaxLevel();

  /**
   * 根据租户ID和ID列表查找部门列表
   */
  List<Department> findByTenantIdAndIdIn(Long tenantId, Collection<Long> ids);

  /**
   * 根据ID列表查找部门ID列表
   */
  @Query(value = "SELECT d.id FROM Department d WHERE d.id IN (?1)")
  List<Long> findIdsByIdIn(Collection<Long> ids);

  /**
   * 根据租户ID删除所有部门
   */
  @Modifying
  void deleteByTenantId(Long tenantId);

}
