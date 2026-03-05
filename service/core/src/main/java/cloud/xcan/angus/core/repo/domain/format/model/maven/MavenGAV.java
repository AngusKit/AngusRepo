package cloud.xcan.angus.core.repo.domain.format.model.maven;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MavenGAV {
  private String groupId;
  private String artifactId;
  private String version;
  private String classifier;
  private String extension;
}
