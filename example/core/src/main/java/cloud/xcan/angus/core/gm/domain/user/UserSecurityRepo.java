package cloud.xcan.angus.core.gm.domain.user;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 用户安全仓储接口
 */
@NoRepositoryBean
public interface UserSecurityRepo extends BaseRepository<UserSecurity, Long> {

  /**
   * 根据用户ID查找安全信息
   */
  UserSecurity findByUserId(Long userId);
}
