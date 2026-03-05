package cloud.xcan.angus.core.repo.domain.format.model.raw;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawAssetMetadata {
  private String path;
  private String fileName;
  private String contentType;
  private Long size;
  private String sha256;
  private String md5;
  private String etag;
  private Boolean isDirectory;
  private LocalDateTime lastModified;
}
