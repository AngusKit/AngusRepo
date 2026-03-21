package cloud.xcan.angus.core.gm.infra.persistence.mysql.user;

import cloud.xcan.angus.core.gm.domain.user.UserTokenRepo;
import org.springframework.stereotype.Repository;

/**
 * <p>用户令牌仓储MySQL实现</p>
 */
@Repository
public interface UserTokenRepoMysql extends UserTokenRepo {
  // 继承领域层接口，Spring会根据配置自动选择实现
}
