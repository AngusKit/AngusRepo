package cloud.xcan.angus.core.repo.application.cmd.notification.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.notification.NotificationCmd;
import cloud.xcan.angus.core.repo.domain.notification.Notification;
import cloud.xcan.angus.core.repo.domain.notification.NotificationRepo;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
            .orElseThrow(() -> new RuntimeException("通知不存在: " + notification.getId()));
      }

      @Override
      protected Notification process() {
        if (notification.getTitle() != null) existing.setTitle(notification.getTitle());
        if (notification.getMessage() != null) existing.setMessage(notification.getMessage());
        if (notification.getType() != null) existing.setType(notification.getType());
        if (notification.getPriority() != null) existing.setPriority(notification.getPriority());
        notificationRepo.save(existing);
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    notificationRepo.deleteById(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteBatch(List<String> ids) {
    notificationRepo.deleteAllById(ids);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void markAsRead(String id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (!notificationRepo.existsById(id)) {
          throw new RuntimeException("通知不存在: " + id);
        }
      }

      @Override
      protected Void process() {
        notificationRepo.markAsRead(PrincipalContext.getTenantId(), id);
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
          throw new RuntimeException("通知不存在: " + id);
        }
      }

      @Override
      protected Void process() {
        notificationRepo.updateStarred(PrincipalContext.getTenantId(), id, true);
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
          throw new RuntimeException("通知不存在: " + id);
        }
      }

      @Override
      protected Void process() {
        notificationRepo.updateStarred(PrincipalContext.getTenantId(), id, false);
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
          throw new RuntimeException("通知不存在: " + id);
        }
      }

      @Override
      protected Void process() {
        notificationRepo.updateArchived(PrincipalContext.getTenantId(), id, true);
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
          throw new RuntimeException("通知不存在: " + id);
        }
      }

      @Override
      protected Void process() {
        notificationRepo.updateArchived(PrincipalContext.getTenantId(), id, false);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Notification, String> getRepository() {
    return this.notificationRepo;
  }
}
