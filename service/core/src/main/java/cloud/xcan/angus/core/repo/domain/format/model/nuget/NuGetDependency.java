package cloud.xcan.angus.core.repo.domain.format.model.nuget;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NuGetDependency {
  private String id;
  private String versionRange;
}
