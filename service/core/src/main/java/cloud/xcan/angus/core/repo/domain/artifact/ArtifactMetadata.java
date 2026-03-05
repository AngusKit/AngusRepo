package cloud.xcan.angus.core.repo.domain.artifact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtifactMetadata {

  private String groupId;

  private String artifactId;

  private String packaging;

  private String digest;

  private Integer layers;

  private String architecture;

  private String os;

  private String scope;

  private String dependencies;

  private String pythonVersion;

  private String classifiers;

  private String authors;

  private String projectUrl;

  public boolean isValid() {
    return (groupId != null && !groupId.trim().isEmpty())
        || (artifactId != null && !artifactId.trim().isEmpty())
        || (digest != null && !digest.trim().isEmpty());
  }
}
