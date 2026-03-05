package cloud.xcan.angus.core.repo.domain.repository;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RepoEntityListRepo extends CustomBaseRepository<RepoEntity> {

  StringBuilder getSqlTemplate0(SearchMode mode, Class<RepoEntity> mainClz,
      Set<SearchCriteria> criteria, String tableName, String... matches);

  String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params);
}
