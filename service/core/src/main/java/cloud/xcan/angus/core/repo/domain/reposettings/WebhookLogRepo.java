package cloud.xcan.angus.core.repo.domain.reposettings;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface WebhookLogRepo extends BaseRepository<WebhookLog, Long> {

  List<WebhookLog> findByWebhookId(Long webhookId);

  long countByWebhookId(Long webhookId);

  long countByWebhookIdAndSuccess(Long webhookId, Boolean success);
}
