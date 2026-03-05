package cloud.xcan.angus.core.repo.domain.format.model.pypi;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PyPIFile {
  private String filename;
  private String url;
  private String packagetype;
  private String pythonVersion;
  private String requiresPython;
  private Long size;
  private Map<String, String> digests;
  private Boolean yanked;
  private String yankedReason;
}
