package cloud.xcan.angus.core.repo.application.cmd.reposettings;

import cloud.xcan.angus.core.repo.domain.reposettings.Webhook;
import cloud.xcan.angus.core.repo.domain.reposettings.WebhookLog;

public interface WebhookCmd {

  Webhook create(Webhook webhook);

  Webhook update(Webhook webhook);

  void updateActive(Long id, Boolean active);

  void delete(Long id);

  WebhookLog test(Long id);
}
