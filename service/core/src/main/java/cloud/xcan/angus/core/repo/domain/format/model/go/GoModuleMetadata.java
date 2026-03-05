package cloud.xcan.angus.core.repo.domain.format.model.go;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoModuleMetadata {
  private String modulePath;
  private String version;
  private LocalDateTime timestamp;
  private String goModContent;
  private String goVersion;
  private List<GoModuleDependency> require;
  private List<String> retract;
  private List<GoModuleDependency> replace;
  private List<GoModuleDependency> exclude;
}
