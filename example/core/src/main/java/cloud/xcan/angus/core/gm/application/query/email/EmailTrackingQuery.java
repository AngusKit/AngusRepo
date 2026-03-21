package cloud.xcan.angus.core.gm.application.query.email;

import cloud.xcan.angus.core.gm.domain.email.EmailTracking;
import java.util.Optional;

public interface EmailTrackingQuery {

  /**
   * 根据邮件ID查找追踪记录
   */
  Optional<EmailTracking> findByEmailId(Long emailId);
}
