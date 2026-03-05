package cloud.xcan.angus.core.repo.infra.persistence.mysql.access;

import cloud.xcan.angus.core.repo.domain.access.AccessLogRepo;
import org.springframework.stereotype.Repository;

@Repository("accessLogRepo")
public interface AccessLogRepoMysql extends AccessLogRepo {


}
