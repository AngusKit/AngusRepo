package cloud.xcan.angus.core.gm.domain.user;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 登录设备仓储接口
 */
@NoRepositoryBean
public interface LoginDeviceRepo extends BaseRepository<LoginDevice, Long> {

  /**
   * 根据用户ID分页查询
   */
  Page<LoginDevice> findByUserIdOrderByLastActiveAtDesc(Long userId, Pageable pageable);

  /**
   * 根据用户ID和设备ID查找
   */
  LoginDevice findByUserIdAndId(Long userId, Long deviceId);

  /**
   * 根据用户ID和设备标识（String）查找
   */
  LoginDevice findByUserIdAndDeviceId(Long userId, String deviceId);

  /**
   * 根据用户ID查找所有设备
   */
  List<LoginDevice> findByUserId(Long userId);

  /**
   * 批量更新用户其他设备的isCurrent为false
   *
   * @param userId   用户ID
   * @param deviceId 当前设备ID（排除此设备）
   */
  @Modifying
  @Query(value = "UPDATE gm_user_login_device SET is_current = false WHERE user_id = ?1 AND device_id != ?2 AND is_current = true", nativeQuery = true)
  void updateOtherDevicesIsCurrentToFalse(Long userId, String deviceId);

  /**
   * 根据用户ID删除
   */
  void deleteByUserId(Long userId);
}
