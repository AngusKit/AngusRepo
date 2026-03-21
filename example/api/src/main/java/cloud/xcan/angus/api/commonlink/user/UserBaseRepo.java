package cloud.xcan.angus.api.commonlink.user;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 用户基础仓储接口
 */
@Repository("commonUserBaseRepo")
public interface UserBaseRepo extends NameJoinRepository<UserBase, Long>,
    BaseRepository<UserBase, Long> {

  /**
   * 根据ID列表查找用户基础信息列表
   */
  @Override
  @Query(value = "SELECT * FROM gm_user WHERE id IN ?1", nativeQuery = true)
  List<UserBase> findByIdIn(Collection<Long> ids);

  /**
   * 根据ID查找用户基础信息
   */
  @Override
  Optional<UserBase> findById(Long id);

  /**
   * 分页查找所有用户ID
   */
  @Query(value = "SELECT id FROM UserBase")
  Page<Long> findAllIds(Pageable pageable);

  /**
   * 根据ID列表查找有效用户基础信息列表（未删除、激活状态、未锁定）
   */
  @Query(value = "SELECT * FROM gm_user u "
      + "WHERE u.id IN (?1) AND u.status = 'ACTIVE' AND u.locked = 0", nativeQuery = true)
  List<UserBase> findValidByIdIn(Collection<Long> ids);


  /**
   * 根据名称列表查找用户基础信息列表
   */
  List<UserBase> findByNameIn(Collection<String> names);

  /**
   * 根据租户ID查找所有用户基础信息列表
   */
  List<UserBase> findAllByTenantId(Long tenantId);

  /**
   * 根据用户名查找用户基础信息
   */
  UserBase findByUsername(String username);

  /**
   * 根据用户名列表查找用户基础信息列表
   */
  List<UserBase> findByUsernameIn(Collection<String> usernames);

  /**
   * 根据邮箱列表查找用户基础信息列表
   */
  List<UserBase> findByEmailIn(Collection<String> emails);

}
