package cloud.xcan.angus.core.repo.domain.format.model.helm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelmMaintainer {
  private String name;
  private String email;
  private String url;
}
