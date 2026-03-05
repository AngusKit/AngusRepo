package cloud.xcan.angus.core.repo.domain.format.model.go;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoModuleDependency {
  private String path;
  private String version;
}
