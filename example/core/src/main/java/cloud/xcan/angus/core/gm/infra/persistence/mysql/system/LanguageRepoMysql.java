package cloud.xcan.angus.core.gm.infra.persistence.mysql.system;

import cloud.xcan.angus.core.gm.domain.system.LanguageRepo;
import org.springframework.stereotype.Repository;

/**
 * <p>支持的语言仓储MySQL实现</p>
 */
@Repository
public interface LanguageRepoMysql extends LanguageRepo {
  // 继承领域层接口，Spring会根据配置自动选择实现
}
