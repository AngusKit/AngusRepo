package cloud.xcan.angus.core.gm.application.cmd.email;

import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.core.gm.domain.email.Email;
import java.util.Map;

public interface EmailCmd {

  Email create(Email email);

  Email send(Long id, boolean async);

  Email sendByTemplate(String templateCode, Language language, String to, String cc, String bcc,
      Map<String, String> params, boolean async);

  Email retry(Long id);

  Email cancel(Long id);

  void delete(Long id);

  void update0(Email email);
}
