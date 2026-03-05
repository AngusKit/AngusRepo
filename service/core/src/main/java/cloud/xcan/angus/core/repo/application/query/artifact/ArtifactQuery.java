package cloud.xcan.angus.core.repo.application.query.artifact;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.domain.artifact.Artifact;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.vo.ArtifactStatisticsVo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ArtifactQuery {

  Page<Artifact> find(GenericSpecification<Artifact> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  Optional<Artifact> findById(Long id);

  Artifact findAndCheck(Long id);

  ArtifactStatisticsVo getStatistics();

  List<Artifact> findVersions(Long id);

  boolean isStarredByUser(Long artifactId, Long userId);
}
