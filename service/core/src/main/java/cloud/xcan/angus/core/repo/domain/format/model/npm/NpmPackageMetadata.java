package cloud.xcan.angus.core.repo.domain.format.model.npm;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NpmPackageMetadata {
  private String name;
  private String scope;
  private String description;
  private Map<String, String> distTags;
  private Map<String, NpmVersionMetadata> versions;
  private String readme;
  private String readmeFilename;
  private String license;
  private String homepage;
  private String repository;
  private String bugs;
  private List<String> keywords;
  private List<NpmMaintainer> maintainers;
  private NpmTimeInfo time;
}
