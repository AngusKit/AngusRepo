package cloud.xcan.angus.core.repo.domain.format.store;

import java.io.InputStream;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Represents cached content from a proxy repository.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CachedContent {

  private String path;
  private String contentType;
  private long size;
  private InputStream inputStream;
  private LocalDateTime cachedAt;
  private LocalDateTime expiresAt;

  public boolean isExpired() {
    return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
  }
}
