package cloud.xcan.angus.core.repo.application.query.reposettings;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.domain.reposettings.RepositoryGlobalSettings;
import cloud.xcan.angus.core.repo.domain.reposettings.Webhook;
import cloud.xcan.angus.core.repo.domain.reposettings.WebhookLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface RepoSettingsQuery {

  Optional<RepositoryGlobalSettings> getSettings();

  Webhook findWebhookById(Long id);

  Page<Webhook> listWebhooks(GenericSpecification<Webhook> spec, PageRequest pageable);

  List<WebhookLog> getWebhookLogs(Long webhookId);
}
