package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLogInfo;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLogSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

/**
 * API请求日志搜索仓储MySQL实现
 */
@Repository
public class InterfaceRequestLogSearchRepoMysql extends
    SimpleSearchRepository<InterfaceRequestLogInfo> implements InterfaceRequestLogSearchRepo {

}
