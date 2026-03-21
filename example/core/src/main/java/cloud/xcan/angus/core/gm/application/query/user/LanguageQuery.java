package cloud.xcan.angus.core.gm.application.query.user;

import cloud.xcan.angus.core.gm.domain.system.Language;
import java.util.List;

/**
 * <p>支持的语言查询服务接口</p>
 */
public interface LanguageQuery {

  /**
   * <p>获取所有启用的语言列表</p>
   */
  List<Language> findEnabledLanguages();

  /**
   * <p>检查语言代码是否支持</p>
   */
  boolean isLanguageSupported(String code);
}
