package cloud.xcan.angus.core.repo.application.cmd.notification.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.notification.NotificationCmd;
import cloud.xcan.angus.core.repo.domain.notification.Notification;
import cloud.xcan.angus.core.repo.domain.notification.NotificationRepo;
import cloud.xcan.angus.remote.message.http.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Biz
public class NotificationCmdImpl extends CommCmd<Notification, String> implements NotificationCmd {

  @Autowired(required = false)
  private NotificationRepo notificationRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Notification create(Notification notification) {
    return new BizTemplate<Notification>() {
      @Override
      protected Notification process() {
        notification.setId(UUID.randomUUID().toString());
        notification.setIsRead(false);
        notification.setIsStarred(false);
        notification.setIsArchived(false);
        notification.setCreatedDate(LocalDateTime.now());
        insert0(notification);
        log.info("Notification created: id={}, title={}, type={}", 
            notification.getId(), notification.getTitle(), notification.getType());
        return notification;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Notification update(Notification notification) {
    return new BizTemplate<Notification>() {
      Notification existing;

      @Override
      protected void checkParams() {
        existing = notificationRepo.findById(notification.getId())
            .orElseThrow(() -> ResourceNotFound.of(notification.getId(), "Notification"));
      }

      @Override
      protected Notification process() {
        if (notification.getTitle() != null) existing.setTitle(notification.getTitle());
        if (notification.getMessage() != null) existing.setMessage(notification.getMessage());
        if (notification.getType() != null) existing.setType(notification.getType());
        if (notification.getPriority() != null) existing.setPriority(notification.getPriority());
        notificationRepo.save(existing);
        log.info("Notification updated: id={}, title={}", existing.getId(), existing.getTitle());
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    notificationRepo.deleteById(id);
    log.warn("Notification deleted: id={}", id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteBatch(List<String> ids) {
    notificationRepo.deleteAllById(ids);
    log.warn("Notifications deleted in batch: count={}", ids.size());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void markAsRead(String id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (!notificationRepo.existsById(id)) {
          throw ResourceNotFound.of(id, "Notification");
        }
      }

      @Override
      protected Void process() {
        notificationRepo.markAsRead(PrincipalContext.getTenantId(), id);
        log.info("Notification marked as read: id={}", id);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void markBatchAsRead(List<String> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        notificationRepo.markBatchAsRead(PrincipalContext.getTenantId(), ids);
        log.info("Batch notifications marked as read: count={}", ids.size());
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void addStar(String id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (!notificationRepo.existsById(id)) {
          throw ResourceNotFound.of(id, "Notification");
        }
      }

      @Override
      protected Void process() {
        notificationRepo.updateStarred(PrincipalContext.getTenantId(), id, true);
        log.info("Notification starred: id={}", id);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void removeStar(String id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (!notificationRepo.existsById(id)) {
          throw ResourceNotFound.of(id, "Notification");
        }
      }

      @Override
      protected Void process() {
        notificationRepo.updateStarred(PrincipalContext.getTenantId(), id, false);
        log.info("Notification unstarred: id={}", id);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void archive(String id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (!notificationRepo.existsById(id)) {
          throw ResourceNotFound.of(id, "Notification");
        }
      }

      @Override
      protected Void process() {
        notificationRepo.updateArchived(PrincipalContext.getTenantId(), id, true);
        log.info("Notification archived: id={}", id);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void unarchive(String id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (!notificationRepo.existsById(id)) {
          throw ResourceNotFound.of(id, "Notification");
        }
      }

      @Override
      protected Void process() {
        notificationRepo.updateArchived(PrincipalContext.getTenantId(), id, false);
        log.info("Notification unarchived: id={}", id);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Notification, String> getRepository() {
    return this.notificationRepo;
  }
}
