package cloud.xcan.angus.core.gm.infra.persistence.mysql.backup;

import cloud.xcan.angus.core.gm.domain.backup.RestoreTaskRepo;
import org.springframework.stereotype.Repository;

@Repository
public interface RestoreTaskRepoMysql extends RestoreTaskRepo {
  // 继承领域层接口，Spring会根据配置自动选择实现
}
