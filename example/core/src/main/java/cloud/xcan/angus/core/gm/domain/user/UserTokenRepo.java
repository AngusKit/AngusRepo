package cloud.xcan.angus.core.gm.domain.user;

import cloud.xcan.angus.api.commonlink.user.enums.TokenStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * <p>用户令牌仓储接口</p>
 */
@NoRepositoryBean
public interface UserTokenRepo extends BaseRepository<UserToken, Long> {

  /**
   * <p>根据用户ID查找所有令牌</p>
   */
  List<UserToken> findByUserId(Long userId);

  /**
   * <p>根据用户ID和状态查找令牌</p>
   */
  List<UserToken> findByUserIdAndStatus(Long userId, TokenStatus status);

  /**
   * <p>根据用户ID和名称查找令牌</p>
   */
  UserToken findByUserIdAndName(Long userId, String name);

  /**
   * <p>统计用户令牌数量</p>
   */
  long countByUserId(Long userId);

  /**
   * <p>根据token值查找令牌</p>
   */
  UserToken findByToken(String token);
}
