package cloud.xcan.angus.core.gm.application.query.email;

import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface EmailTemplateQuery {

  /**
   * 根据ID查找模板并检查是否存在
   */
  EmailTemplate findAndCheck(Long id);

  /**
   * 根据编码查找模板并检查是否存在并且启用
   */
  EmailTemplate findAndCheckValid(String code, Language language);

  /**
   * 分页查找模板
   */
  Page<EmailTemplate> find(GenericSpecification<EmailTemplate> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  /**
   * 检查模板编码和语言组合是否存在
   */
  boolean existsByCodeAndLanguage(String code, Language language);

  /**
   * 检查模板编码和语言组合是否存在（排除指定ID）
   */
  boolean existsByCodeAndLanguageAndIdNot(String code, Language language, Long id);

  /**
   * 批量设置模板统计信息（使用次数、打开率、点击率）
   */
  void assembleTemplateInfos(List<EmailTemplate> templates);
}

