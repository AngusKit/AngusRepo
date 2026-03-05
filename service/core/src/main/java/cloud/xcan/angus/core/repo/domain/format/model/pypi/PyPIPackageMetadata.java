package cloud.xcan.angus.core.repo.domain.format.model.pypi;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PyPIPackageMetadata {
  private String name;
  private String version;
  private String summary;
  private String description;
  private String descriptionContentType;
  private String author;
  private String authorEmail;
  private String maintainer;
  private String maintainerEmail;
  private String license;
  private String homepage;
  private String projectUrl;
  private String requiresPython;
  private List<String> classifiers;
  private List<String> keywords;
  private List<String> requiresDist;
  private List<PyPIFile> files;
}
