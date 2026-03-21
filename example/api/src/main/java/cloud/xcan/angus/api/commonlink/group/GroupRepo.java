package cloud.xcan.angus.api.commonlink.group;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.group.enums.GroupType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

/**
 * 用户组仓储接口
 */
@Repository("commonGroupRepo")
public interface GroupRepo extends NameJoinRepository<Group, Long>, BaseRepository<Group, Long> {

  /**
   * 根据租户ID查找所有用户组
   */
  List<Group> findAllByTenantId(Long tenantId);

  /**
   * 根据ID列表查找用户组ID列表
   */
  List<Long> findIdsByIdIn(Collection<Long> ids);

  /**
   * 检查名称是否存在
   */
  boolean existsByName(String name);

  /**
   * 检查名称是否存在（排除指定ID）
   */
  boolean existsByNameAndIdNot(String name, Long id);

  /**
   * 检查编码是否存在
   */
  boolean existsByCode(String code);

  /**
   * 检查编码是否存在（排除指定ID）
   */
  boolean existsByCodeAndIdNot(String code, Long id);

  /**
   * 统计指定状态的用户组数量
   */
  long countByStatus(EnabledStatus status);

  /**
   * 统计指定类型的用户组数量
   */
  long countByType(GroupType type);

  /**
   * 统计租户下的用户组数量
   */
  long countByTenantId(Long tenantId);

  /**
   * 根据租户ID删除所有组
   */
  @Modifying
  void deleteByTenantId(Long tenantId);

}
