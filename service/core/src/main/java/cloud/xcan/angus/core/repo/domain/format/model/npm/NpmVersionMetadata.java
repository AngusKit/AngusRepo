package cloud.xcan.angus.core.repo.domain.format.model.npm;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NpmVersionMetadata {
  private String name;
  private String version;
  private String description;
  private String main;
  private String module;
  private String types;
  private Map<String, String> dependencies;
  private Map<String, String> devDependencies;
  private Map<String, String> peerDependencies;
  private Map<String, String> scripts;
  private NpmDist dist;
  private String license;
  private List<String> engines;
}
