package cloud.xcan.angus.core.repo.domain.format.model.yum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpmChangelog {
  private String author;
  private Long date;
  private String text;
}
