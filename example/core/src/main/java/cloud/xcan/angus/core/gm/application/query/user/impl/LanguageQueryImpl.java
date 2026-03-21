package cloud.xcan.angus.core.gm.application.query.user.impl;

import cloud.xcan.angus.core.gm.application.query.user.LanguageQuery;
import cloud.xcan.angus.core.gm.domain.system.Language;
import cloud.xcan.angus.core.gm.domain.system.LanguageRepo;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * <p>支持的语言查询服务实现</p>
 */
@Service
public class LanguageQueryImpl implements LanguageQuery {

  @Resource
  private LanguageRepo languageRepo;

  @Override
  public List<Language> findEnabledLanguages() {
    return languageRepo.findByEnabledTrueOrderBySortOrderAsc();
  }

  @Override
  public boolean isLanguageSupported(String code) {
    if (code == null || code.isEmpty()) {
      return false;
    }
    Language language = languageRepo.findByCode(code);
    return language != null && Boolean.TRUE.equals(language.getEnabled());
  }
}
