package cloud.xcan.angus.core.repo.domain.format.model.npm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NpmMaintainer {
  private String name;
  private String email;
}
