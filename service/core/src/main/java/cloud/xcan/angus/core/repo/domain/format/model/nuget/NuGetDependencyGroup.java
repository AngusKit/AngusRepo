package cloud.xcan.angus.core.repo.domain.format.model.nuget;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NuGetDependencyGroup {
  private String targetFramework;
  private List<NuGetDependency> dependencies;
}
