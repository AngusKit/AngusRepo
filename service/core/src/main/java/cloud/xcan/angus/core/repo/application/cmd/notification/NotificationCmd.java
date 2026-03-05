package cloud.xcan.angus.core.repo.application.cmd.notification;

import cloud.xcan.angus.core.repo.domain.notification.Notification;
import java.util.List;

public interface NotificationCmd {
  Notification create(Notification notification);
  Notification update(Notification notification);
  void delete(String id);
  void deleteBatch(List<String> ids);
  void markAsRead(String id);
  void markBatchAsRead(List<String> ids);
  void addStar(String id);
  void removeStar(String id);
  void archive(String id);
  void unarchive(String id);
}
