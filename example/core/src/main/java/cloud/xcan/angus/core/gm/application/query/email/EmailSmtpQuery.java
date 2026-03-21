package cloud.xcan.angus.core.gm.application.query.email;

import cloud.xcan.angus.core.gm.domain.email.EmailSmtp;
import java.util.Optional;

public interface EmailSmtpQuery {

  /**
   * 查找默认SMTP配置
   */
  Optional<EmailSmtp> findDefault();
}

