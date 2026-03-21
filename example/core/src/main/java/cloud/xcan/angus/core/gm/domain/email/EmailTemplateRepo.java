package cloud.xcan.angus.core.gm.domain.email;

import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface EmailTemplateRepo extends NameJoinRepository<EmailTemplate, Long>,
    BaseRepository<EmailTemplate, Long> {

  /**
   * 检查模板编码和语言组合是否存在
   */
  boolean existsByCodeAndLanguage(String code, Language language);

  /**
   * 检查模板编码和语言组合是否存在（排除指定ID）
   */
  boolean existsByCodeAndLanguageAndIdNot(String code, Language language, Long id);

  /**
   * 根据编码和语言查找模板
   */
  EmailTemplate findByCodeAndLanguage(String code, Language language);
}

