package cloud.xcan.angus.core.gm.infra.persistence.postgres.interfaces;

import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLogInfoRepo;
import org.springframework.stereotype.Repository;

/**
 * API请求日志仓储PostgreSQL实现
 */
@Repository
public interface InterfaceRequestLogInfoRepoPostgres extends InterfaceRequestLogInfoRepo {

}
