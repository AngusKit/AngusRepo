package cloud.xcan.angus.core.gm.domain.sms;

import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SmsTemplateRepo extends NameJoinRepository<SmsTemplate, Long>,
    BaseRepository<SmsTemplate, Long> {

  boolean existsByProviderAndCodeAndLanguage(String provider, String code, Language language);

  boolean existsByProviderAndCodeAndLanguageAndIdNot(String provider, String code,
      Language language,
      Long id);

  List<SmsTemplate> findAllByCode(String code);

  List<SmsTemplate> findAllByCodeAndLanguage(String code, Language language);

  Optional<SmsTemplate> findByProviderAndCodeAndLanguage(String provider, String code,
      Language language);

}

