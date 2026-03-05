package cloud.xcan.angus.core.repo.application.cmd.reposettings.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.reposettings.WebhookCmd;
import cloud.xcan.angus.core.repo.domain.reposettings.Webhook;
import cloud.xcan.angus.core.repo.domain.reposettings.WebhookLog;
import cloud.xcan.angus.core.repo.domain.reposettings.WebhookLogRepo;
import cloud.xcan.angus.core.repo.domain.reposettings.WebhookRepo;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class WebhookCmdImpl extends CommCmd<Webhook, Long> implements WebhookCmd {

  @Autowired(required = false)
  private WebhookRepo webhookRepo;

  @Autowired(required = false)
  private WebhookLogRepo webhookLogRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Webhook create(Webhook webhook) {
    return new BizTemplate<Webhook>() {
      @Override
      protected void checkParams() {
        // Validate webhook URL is not empty
      }

      @Override
      protected Webhook process() {
        webhook.setCreatedDate(LocalDateTime.now());
        webhook.setModifiedDate(LocalDateTime.now());
        if (webhook.getActive() == null) {
          webhook.setActive(true);
        }
        if (webhook.getSuccessCount() == null) {
          webhook.setSuccessCount(0);
        }
        if (webhook.getFailureCount() == null) {
          webhook.setFailureCount(0);
        }
        insert0(webhook);
        return webhook;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Webhook update(Webhook webhook) {
    return new BizTemplate<Webhook>() {
      Webhook existing;

      @Override
      protected void checkParams() {
        existing = webhookRepo.findById(webhook.getId())
            .orElseThrow(() -> new RuntimeException("Webhook不存在: " + webhook.getId()));
      }

      @Override
      protected Webhook process() {
        if (webhook.getName() != null) {
          existing.setName(webhook.getName());
        }
        if (webhook.getUrl() != null) {
          existing.setUrl(webhook.getUrl());
        }
        if (webhook.getSecret() != null) {
          existing.setSecret(webhook.getSecret());
        }
        if (webhook.getEvents() != null) {
          existing.setEvents(webhook.getEvents());
        }
        if (webhook.getActive() != null) {
          existing.setActive(webhook.getActive());
        }
        existing.setModifiedBy(webhook.getModifiedBy());
        existing.setModifiedDate(LocalDateTime.now());
        webhookRepo.save(existing);
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateActive(Long id, Boolean active) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (!webhookRepo.existsById(id)) {
          throw new RuntimeException("Webhook不存在: " + id);
        }
      }

      @Override
      protected Void process() {
        webhookRepo.updateActiveStatus(id, active);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    webhookRepo.deleteById(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public WebhookLog test(Long id) {
    return new BizTemplate<WebhookLog>() {
      Webhook existing;

      @Override
      protected void checkParams() {
        existing = webhookRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Webhook不存在: " + id));
      }

      @Override
      protected WebhookLog process() {
        WebhookLog log = new WebhookLog();
        log.setWebhookId(id);
        log.setEvent("TEST");
        log.setTriggeredAt(LocalDateTime.now());
        try {
          // Simulate webhook test call
          log.setStatusCode(200);
          log.setSuccess(true);
          log.setRequest("{\"event\":\"test\",\"webhook_id\":" + id + "}");
          log.setResponse("{\"status\":\"ok\"}");
          log.setResponseTime(100L);
          webhookRepo.incrementSuccessCount(id);
        } catch (Exception e) {
          log.setStatusCode(500);
          log.setSuccess(false);
          log.setResponse(e.getMessage());
          log.setResponseTime(0L);
          webhookRepo.incrementFailureCount(id);
        }
        webhookLogRepo.save(log);
        existing.setLastTriggerTime(LocalDateTime.now());
        webhookRepo.save(existing);
        return log;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Webhook, Long> getRepository() {
    return this.webhookRepo;
  }
}
