package cloud.xcan.angus.core.repo.infra.persistence.postgres.reposettings;

import cloud.xcan.angus.core.repo.domain.reposettings.WebhookLogRepo;
import org.springframework.stereotype.Repository;

@Repository("webhookLogRepo")
public interface WebhookLogRepoPostgres extends WebhookLogRepo {


}
