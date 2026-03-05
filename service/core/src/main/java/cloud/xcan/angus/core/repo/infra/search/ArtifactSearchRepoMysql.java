package cloud.xcan.angus.core.repo.infra.search;

import cloud.xcan.angus.core.jpa.repository.AbstractSearchRepository;
import cloud.xcan.angus.core.jpa.repository.SearchMode;
import cloud.xcan.angus.core.repo.domain.artifact.Artifact;
import cloud.xcan.angus.core.repo.domain.artifact.ArtifactListRepo;
import cloud.xcan.angus.core.repo.domain.artifact.ArtifactSearchRepo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class ArtifactSearchRepoMysql extends AbstractSearchRepository<Artifact>
    implements ArtifactSearchRepo {

  @Resource
  private ArtifactListRepo artifactListRepo;

  @Override
  public StringBuilder getSqlTemplate(Set<SearchCriteria> criteria, Class<Artifact> mainClz,
      Object[] objects, String... matches) {
    return artifactListRepo.getSqlTemplate0(getSearchMode(), mainClz, criteria,
        "artifact", matches);
  }

  @Override
  public String getReturnFieldsCondition(Set<SearchCriteria> criteria, Object[] params) {
    return artifactListRepo.getReturnFieldsCondition(criteria, params);
  }

  @Override
  public SearchMode getSearchMode() {
    return SearchMode.MATCH;
  }
}
