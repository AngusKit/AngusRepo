package cloud.xcan.angus.core.repo.infra.search;

import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.repo.domain.access.AccessRule;
import cloud.xcan.angus.core.repo.domain.access.AccessRuleListRepo;
import cloud.xcan.angus.core.repo.domain.access.AccessRuleSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class AccessRuleSearchRepoMysql extends AbstractSearchRepository<AccessRule>
    implements AccessRuleSearchRepo {

  @Resource
  private AccessRuleListRepo accessRuleListRepo;

  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<AccessRule> mainClz,
      Object[] objects, String... matches) {
    return accessRuleListRepo.getSqlTemplate0(getSearchMode(), mainClz, criteria,
        "access_rule", matches);
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return accessRuleListRepo.getReturnFieldsCondition(criteria, params);
  }

  @Override
  public SearchMode getSearchMode() {
    return SearchMode.MATCH;
  }
}
