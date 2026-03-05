package cloud.xcan.angus.core.repo.infra.persistence.postgres.access;

import cloud.xcan.angus.core.repo.domain.access.AccessLogRepo;
import org.springframework.stereotype.Repository;

@Repository("accessLogRepo")
public interface AccessLogRepoPostgres extends AccessLogRepo {


}
