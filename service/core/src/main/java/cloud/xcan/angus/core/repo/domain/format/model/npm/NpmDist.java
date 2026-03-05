package cloud.xcan.angus.core.repo.domain.format.model.npm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NpmDist {
  private String tarball;
  private String shasum;
  private String integrity;
  private Long unpackedSize;
  private Integer fileCount;
}
