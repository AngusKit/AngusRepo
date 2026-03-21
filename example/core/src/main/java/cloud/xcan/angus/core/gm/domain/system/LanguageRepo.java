package cloud.xcan.angus.core.gm.domain.system;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * <p>支持的语言仓储接口</p>
 */
@NoRepositoryBean
public interface LanguageRepo extends BaseRepository<Language, Long> {

  /**
   * <p>查找所有启用的语言，按排序顺序排序</p>
   */
  List<Language> findByEnabledTrueOrderBySortOrderAsc();

  /**
   * <p>根据代码查找</p>
   */
  Language findByCode(String code);
}
