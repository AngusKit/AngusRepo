package cloud.xcan.angus.core.gm.application.cmd.sms;

import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import java.util.List;
import java.util.Map;

public interface SmsCmd {

  Sms send(String templateCode, Language language, String phone, Map<String, String> params);

  List<Sms> sendBatch(String templateCode, Language language, List<String> phones,
      Map<String, String> params);

  Sms test(String phone, String content);

}
