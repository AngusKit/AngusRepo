package cloud.xcan.angus.core.repo.infra.persistence.postgres.activity;

import cloud.xcan.angus.core.repo.domain.activity.ActivityRepo;
import org.springframework.stereotype.Repository;

@Repository("activityRepo")
public interface ActivityRepoPostgres extends ActivityRepo {


}
