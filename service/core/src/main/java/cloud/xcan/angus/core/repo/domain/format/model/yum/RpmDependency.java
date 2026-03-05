package cloud.xcan.angus.core.repo.domain.format.model.yum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpmDependency {
  private String name;
  private String flags;
  private String epoch;
  private String version;
  private String release;
}
