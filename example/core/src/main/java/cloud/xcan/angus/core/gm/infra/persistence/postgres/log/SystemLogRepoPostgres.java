package cloud.xcan.angus.core.gm.infra.persistence.postgres.log;

import cloud.xcan.angus.core.gm.domain.log.SystemLogRepo;
import org.springframework.stereotype.Repository;

/**
 * 系统日志仓储PostgreSQL实现
 */
@Repository
public interface SystemLogRepoPostgres extends SystemLogRepo {
  // 继承领域层接口，Spring会根据配置自动选择实现
}
