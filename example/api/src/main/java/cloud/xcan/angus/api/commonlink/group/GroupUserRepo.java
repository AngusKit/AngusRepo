package cloud.xcan.angus.api.commonlink.group;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 用户组成员关系仓储接口
 */
@Repository("commonGroupUserRepo")
public interface GroupUserRepo extends BaseRepository<GroupUser, Long> {

  /**
   * 根据用户组ID列表查找用户组成员关系列表
   */
  List<GroupUser> findAllByGroupIdIn(List<Long> groupIds);

  /**
   * 根据用户组ID查找用户组成员关系列表
   */
  List<GroupUser> findAllByGroupId(Long groupId);

  /**
   * 根据租户ID查找用户组成员关系列表
   */
  List<GroupUser> findByTenantId(Long tenantId);

  /**
   * 根据用户ID查找用户组成员关系列表
   */
  List<GroupUser> findAllByUserId(Long userId);

  /**
   * 检查用户组成员关系是否存在
   */
  boolean existsByGroupIdAndUserId(Long groupId, Long userId);

  /**
   * 根据用户组ID列表查找有效用户的邮箱列表（分页）
   */
  @Query(value = "SELECT u.email FROM gm_user u INNER JOIN gm_group_user gu ON u.id = gu.user_id "
      + "AND gu.group_id IN (?1) AND u.deleted = 0 AND u.status = 'ACTIVE' ORDER BY u.id ASC LIMIT ?2,?3", nativeQuery = true)
  List<String> findValidEmailByGroupIds(Collection<Long> groupIds, int offset, int size);

  /**
   * 根据用户组ID列表查找有效用户的手机号列表（分页）
   */
  @Query(value = "SELECT u.mobile FROM gm_user u INNER JOIN gm_group_user gu ON u.id = gu.user_id "
      + "AND gu.group_id IN (?1) AND u.deleted = 0 AND u.status = 'ACTIVE' ORDER BY u.id ASC LIMIT ?2,?3", nativeQuery = true)
  List<String> findValidMobileByGroupIds(Collection<Long> groupIds, int offset, int size);

  /**
   * 根据用户组ID列表查找有效用户ID集合
   */
  @Query(value = "SELECT u.id FROM gm_group_user gu INNER JOIN gm_user u ON gu.user_id = u.id "
      + "AND gu.group_id IN (?1) AND u.deleted = 0 AND u.status = 'ACTIVE'", nativeQuery = true)
  Set<Long> findValidUserIdsByGroupIds(Collection<Long> groupIds);

  /**
   * 根据租户ID和用户组ID列表查找有效用户ID集合
   */
  @Query(value = "SELECT u.id FROM gm_group_user gu INNER JOIN gm_user u ON gu.user_id = u.id "
      + "AND gu.tenant_id = ?1 AND u.tenant_id = ?1 AND gu.group_id IN (?2) AND u.deleted = 0 AND u.status = 'ACTIVE'", nativeQuery = true)
  Set<Long> findValidUserIdsByTenantIdAndGroupIds(Long tenantId, Collection<Long> groupIds);

  /**
   * 根据用户组ID列表和在线状态查找用户名集合
   */
  @Query(value =
      "SELECT u.username FROM gm_group_user gu INNER JOIN gm_user u ON du.user_id = u.id "
          + "AND gu.group_id IN (?1) AND u.deleted = 0 AND u.status = 'ACTIVE' AND u.online = ?2", nativeQuery = true)
  Set<String> findUsernamesByGroupIdInAndOnline(Collection<Long> groupIds, Boolean online);

  /**
   * 根据用户组ID列表查找用户ID集合
   */
  @Query(value = "SELECT gu.user_id FROM gm_group_user gu WHERE gu.group_id IN (?1) ", nativeQuery = true)
  Set<Long> findUserIdsByGroupIds(Collection<Long> groupIds);

  /**
   * 根据用户组ID列表分组统计用户数量
   */
  @Query(value = "SELECT gu.group_id, COUNT(gu.user_id) as user_count FROM gm_group_user gu WHERE gu.group_id IN (?1) GROUP BY gu.group_id", nativeQuery = true)
  List<Object[]> countGroupByGroupIds(Collection<Long> groupIds);

  /**
   * 根据用户组ID和用户ID列表批量删除用户组成员关系
   */
  @Modifying
  @Query("DELETE FROM GroupUser ug WHERE ug.groupId = ?1 AND ug.userId in (?2)")
  void deleteByGroupIdAndUserId(Long groupId, Collection<Long> userIds);

  /**
   * 根据用户ID列表批量删除用户组成员关系
   */
  @Modifying
  @Query("DELETE FROM GroupUser ug WHERE ug.userId in (?1)")
  void deleteAllByUserIdIn(Collection<Long> userIds);

  /**
   * 根据用户组ID和用户ID集合批量删除用户组成员关系
   */
  @Modifying
  @Query("DELETE FROM GroupUser ud WHERE ud.groupId = ?1 AND ud.userId in ?2")
  void deleteByGroupIdAndUserIdIn(Long groupId, Set<Long> userIds);

  /**
   * 根据用户组ID集合和用户ID批量删除用户组成员关系
   */
  @Modifying
  @Query("DELETE FROM GroupUser ud WHERE ud.groupId IN (?1) AND ud.userId = ?2")
  void deleteByGroupIdInAndUserId(Set<Long> groupIds, Long userId);

  /**
   * 根据用户组ID集合批量删除用户组成员关系
   */
  @Modifying
  @Query("DELETE FROM GroupUser ug WHERE ug.groupId IN (?1)")
  void deleteAllByGroupIdIn(Set<Long> groupIds);

  /**
   * 根据租户ID集合批量删除用户组成员关系
   */
  @Modifying
  void deleteByTenantId(Long tenantId);

}
