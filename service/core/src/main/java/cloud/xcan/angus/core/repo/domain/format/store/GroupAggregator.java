package cloud.xcan.angus.core.repo.domain.format.store;

import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import java.io.InputStream;
import java.util.Optional;

/**
 * Group repository aggregation framework.
 * Handles aggregation of member repositories for group repository types.
 */
public interface GroupAggregator {

  /**
   * Merge index files from member repositories.
   */
  byte[] mergeIndex(RepoEntity groupRepo);

  /**
   * Resolve an artifact from member repositories.
   * Searches each member in order and returns the first match.
   */
  Optional<InputStream> resolveArtifact(RepoEntity groupRepo, String path);
}
