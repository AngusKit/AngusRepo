package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.log.SystemLog;
import cloud.xcan.angus.core.gm.domain.log.SystemLogSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

/**
 * 系统日志搜索仓储MySQL实现
 */
@Repository
public class SystemLogSearchRepoMysql extends SimpleSearchRepository<SystemLog>
    implements SystemLogSearchRepo {

}
