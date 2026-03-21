package cloud.xcan.angus.core.gm.infra.persistence.postgres.user;

import cloud.xcan.angus.core.gm.domain.user.UserTokenRepo;
import org.springframework.stereotype.Repository;

/**
 * <p>用户令牌仓储PostgreSQL实现</p>
 */
@Repository
public interface UserTokenRepoPostgres extends UserTokenRepo {
  // 继承领域层接口，Spring会根据配置自动选择实现
}
