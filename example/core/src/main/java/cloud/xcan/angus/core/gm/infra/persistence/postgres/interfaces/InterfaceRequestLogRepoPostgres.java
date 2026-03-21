package cloud.xcan.angus.core.gm.infra.persistence.postgres.interfaces;

import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLogRepo;
import org.springframework.stereotype.Repository;

/**
 * API请求日志仓储PostgreSQL实现
 */
@Repository
public interface InterfaceRequestLogRepoPostgres extends InterfaceRequestLogRepo {
  // 继承领域层接口，Spring会根据配置自动选择实现
}
