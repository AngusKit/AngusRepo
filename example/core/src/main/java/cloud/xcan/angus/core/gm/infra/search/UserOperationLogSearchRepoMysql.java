package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.log.UserOperationLog;
import cloud.xcan.angus.core.gm.domain.log.UserOperationLogSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户操作日志搜索仓储MySQL实现
 */
@Repository
public class UserOperationLogSearchRepoMysql extends SimpleSearchRepository<UserOperationLog>
    implements UserOperationLogSearchRepo {

}
