package cloud.xcan.angus.core.repo.application.cmd.artifact;

import cloud.xcan.angus.core.repo.domain.artifact.Artifact;
import java.util.List;

public interface ArtifactCmd {

  Artifact create(Artifact artifact);

  Artifact update(Artifact artifact);

  void markLatest(Long id);

  void delete(Long id);

  void deleteBatch(List<Long> ids);

  void incrementDownloads(Long id);

  void addStar(Long artifactId, Long userId);

  void removeStar(Long artifactId, Long userId);
}
