package cloud.xcan.angus.core.gm.application.query.sms;

import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.core.gm.domain.sms.SmsTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface SmsTemplateQuery {

  SmsTemplate findAndCheck(Long id);

  Page<SmsTemplate> findTemplates(GenericSpecification<SmsTemplate> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  SmsTemplate findAndCheck(String code);

  SmsTemplate findAndCheck(String code, Language language);

  SmsTemplate findAndCheck(String provider, String code, Language language);

  void assembleTemplateInfos(java.util.List<SmsTemplate> templates);
}
