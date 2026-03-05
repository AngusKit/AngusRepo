package cloud.xcan.angus.core.repo.application.query.reposettings.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.application.query.reposettings.RepoSettingsQuery;
import cloud.xcan.angus.core.repo.domain.reposettings.RepositoryGlobalSettings;
import cloud.xcan.angus.core.repo.domain.reposettings.RepositoryGlobalSettingsRepo;
import cloud.xcan.angus.core.repo.domain.reposettings.Webhook;
import cloud.xcan.angus.core.repo.domain.reposettings.WebhookLog;
import cloud.xcan.angus.core.repo.domain.reposettings.WebhookLogRepo;
import cloud.xcan.angus.core.repo.domain.reposettings.WebhookRepo;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class RepoSettingsQueryImpl implements RepoSettingsQuery {

  @Resource
  private RepositoryGlobalSettingsRepo settingsRepo;

  @Resource
  private WebhookRepo webhookRepo;

  @Resource
  private WebhookLogRepo webhookLogRepo;

  @Override
  public Optional<RepositoryGlobalSettings> getSettings() {
    return settingsRepo.findFirstByOrderByIdDesc();
  }

  @Override
  public Webhook findWebhookById(Long id) {
    return webhookRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Webhook不存在: " + id));
  }

  @Override
  public Page<Webhook> listWebhooks(GenericSpecification<Webhook> spec, PageRequest pageable) {
    return new BizTemplate<Page<Webhook>>() {
      @Override
      protected Page<Webhook> process() {
        return webhookRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public List<WebhookLog> getWebhookLogs(Long webhookId) {
    return new BizTemplate<List<WebhookLog>>() {
      @Override
      protected void checkParams() {
        if (!webhookRepo.existsById(webhookId)) {
          throw new RuntimeException("Webhook不存在: " + webhookId);
        }
      }

      @Override
      protected List<WebhookLog> process() {
        return webhookLogRepo.findByWebhookId(webhookId);
      }
    }.execute();
  }
}
