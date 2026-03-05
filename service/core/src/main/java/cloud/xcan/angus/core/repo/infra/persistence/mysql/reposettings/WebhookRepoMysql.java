package cloud.xcan.angus.core.repo.infra.persistence.mysql.reposettings;

import cloud.xcan.angus.core.repo.domain.reposettings.WebhookRepo;
import org.springframework.stereotype.Repository;

@Repository("webhookRepo")
public interface WebhookRepoMysql extends WebhookRepo {


}
