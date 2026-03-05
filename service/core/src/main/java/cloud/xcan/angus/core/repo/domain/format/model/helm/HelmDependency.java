package cloud.xcan.angus.core.repo.domain.format.model.helm;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelmDependency {
  private String name;
  private String version;
  private String repository;
  private String condition;
  private List<String> tags;
  private String alias;
}
