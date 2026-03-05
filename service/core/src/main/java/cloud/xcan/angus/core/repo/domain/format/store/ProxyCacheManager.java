package cloud.xcan.angus.core.repo.domain.format.store;

import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import java.io.InputStream;
import java.time.Duration;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

/**
 * Proxy cache framework.
 * Manages caching for proxy repository types.
 */
public interface ProxyCacheManager {

  /**
   * Get content from cache.
   */
  Optional<CachedContent> getFromCache(String repositoryId, String path);

  /**
   * Put content into cache.
   */
  void putToCache(String repositoryId, String path, InputStream data, Duration ttl);

  /**
   * Invalidate cached content.
   */
  void invalidateCache(String repositoryId, String path);

  /**
   * Proxy a request to the remote repository.
   */
  ResponseEntity<?> proxyRequest(RepoEntity repository, String path, HttpServletRequest request);
}
