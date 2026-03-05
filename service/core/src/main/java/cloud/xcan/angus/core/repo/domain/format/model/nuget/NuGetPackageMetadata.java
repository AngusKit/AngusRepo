package cloud.xcan.angus.core.repo.domain.format.model.nuget;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NuGetPackageMetadata {
  private String id;
  private String version;
  private String title;
  private String description;
  private String summary;
  private String authors;
  private String owners;
  private String projectUrl;
  private String iconUrl;
  private String licenseUrl;
  private String license;
  private String copyright;
  private String tags;
  private String releaseNotes;
  private Boolean requireLicenseAcceptance;
  private List<NuGetDependencyGroup> dependencyGroups;
  private List<NuGetFrameworkAssembly> frameworkAssemblies;
  private String minClientVersion;
  private Boolean isPrerelease;
}
