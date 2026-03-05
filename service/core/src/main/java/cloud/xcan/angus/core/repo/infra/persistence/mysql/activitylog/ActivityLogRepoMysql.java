package cloud.xcan.angus.core.repo.infra.persistence.mysql.activitylog;

import cloud.xcan.angus.core.repo.domain.activitylog.ActivityLogRepo;
import org.springframework.stereotype.Repository;

@Repository("activityLogRepo")
public interface ActivityLogRepoMysql extends ActivityLogRepo {

}
