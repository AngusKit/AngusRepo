package cloud.xcan.angus.core.gm.infra.persistence.postgres.system;

import cloud.xcan.angus.core.gm.domain.system.LanguageRepo;
import org.springframework.stereotype.Repository;

/**
 * <p>支持的语言仓储PostgreSQL实现</p>
 */
@Repository
public interface LanguageRepoPostgres extends LanguageRepo {
  // 继承领域层接口，Spring会根据配置自动选择实现
}
