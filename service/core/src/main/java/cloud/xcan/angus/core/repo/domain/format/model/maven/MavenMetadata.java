package cloud.xcan.angus.core.repo.domain.format.model.maven;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MavenMetadata {
  private String groupId;
  private String artifactId;
  private String version;
  private String packaging;
  private String classifier;
  private String extension;
  private String snapshotTimestamp;
  private Integer snapshotBuildNumber;
  private List<MavenDependency> dependencies;
  private MavenParent parent;
}
