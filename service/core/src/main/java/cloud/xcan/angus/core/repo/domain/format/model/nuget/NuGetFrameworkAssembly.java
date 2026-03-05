package cloud.xcan.angus.core.repo.domain.format.model.nuget;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NuGetFrameworkAssembly {
  private String assemblyName;
  private String targetFramework;
}
