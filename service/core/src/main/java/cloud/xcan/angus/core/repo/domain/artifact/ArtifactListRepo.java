package cloud.xcan.angus.core.repo.domain.artifact;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ArtifactListRepo extends CustomBaseRepository<Artifact> {

  StringBuilder getSqlTemplate0(SearchMode mode, Class<Artifact> mainClz,
      Set<SearchCriteria> criteria, String tableName, String... matches);

  String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params);
}
