package cloud.xcan.angus.core.repo.infra.persistence.mysql.cleanup;

import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicy;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicyListRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class CleanupPolicyListRepoMysql extends AbstractSearchRepository<CleanupPolicy>
    implements CleanupPolicyListRepo {

  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria,
      Class<CleanupPolicy> mainClz, Object[] objects, String... matches) {
    return getSqlTemplate0(getSearchMode(), mainClz, criteria, "cleanup_policy", matches);
  }

  @Override
  public StringBuilder getSqlTemplate0(SearchMode mode, Class<CleanupPolicy> mainClz,
      Set<SearchCriteria> criteria, String tableName, String... matches) {
    String mainAlis = "a";
    StringBuilder sql = new StringBuilder(
        "SELECT %s FROM " + tableName + " " + mainAlis + " WHERE 1=1 ");
    sql.append(getCriteriaAliasCondition(criteria, mainClz, mainAlis, mode, false, matches));
    return sql;
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return "a.*";
  }
}
