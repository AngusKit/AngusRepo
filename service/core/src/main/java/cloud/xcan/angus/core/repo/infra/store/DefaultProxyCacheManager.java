package cloud.xcan.angus.core.repo.infra.store;

import cloud.xcan.angus.core.repo.domain.format.store.BlobStore;
import cloud.xcan.angus.core.repo.domain.format.store.CachedContent;
import cloud.xcan.angus.core.repo.domain.format.store.ProxyCacheManager;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Default implementation of {@link ProxyCacheManager}.
 *
 * <p>Uses the local {@link BlobStore} as a cache layer for proxy repositories.
 * Cached content is stored under the {@code _cache/} prefix in the blob store.
 */
@Slf4j
@Component
public class DefaultProxyCacheManager implements ProxyCacheManager {

  private static final String CACHE_PREFIX = "_cache/";
  private static final String CACHE_TENANT = "_proxy";

  private final BlobStore blobStore;
  private final RestTemplate restTemplate;

  public DefaultProxyCacheManager(BlobStore blobStore) {
    this.blobStore = blobStore;
    this.restTemplate = new RestTemplate();
  }

  @Override
  public Optional<CachedContent> getFromCache(String repositoryId, String path) {
    String cachePath = CACHE_PREFIX + path;
    if (!blobStore.exists(CACHE_TENANT, repositoryId, cachePath)) {
      log.debug("Cache miss for repository={}, path={}", repositoryId, path);
      return Optional.empty();
    }
    log.debug("Cache hit for repository={}, path={}", repositoryId, path);
    InputStream inputStream = blobStore.retrieve(CACHE_TENANT, repositoryId, cachePath);
    long size = blobStore.size(CACHE_TENANT, repositoryId, cachePath);
    CachedContent content = new CachedContent();
    content.setPath(path);
    content.setSize(size);
    content.setInputStream(inputStream);
    // Approximation: blob store does not track cache metadata timestamps
    content.setCachedAt(LocalDateTime.now());
    return Optional.of(content);
  }

  @Override
  public void putToCache(String repositoryId, String path, InputStream data, Duration ttl) {
    String cachePath = CACHE_PREFIX + path;
    log.debug("Caching content for repository={}, path={}, ttl={}", repositoryId, path, ttl);
    blobStore.store(CACHE_TENANT, repositoryId, cachePath, data);
  }

  @Override
  public void invalidateCache(String repositoryId, String path) {
    String cachePath = CACHE_PREFIX + path;
    log.debug("Invalidating cache for repository={}, path={}", repositoryId, path);
    blobStore.delete(CACHE_TENANT, repositoryId, cachePath);
  }

  @Override
  public ResponseEntity<?> proxyRequest(RepoEntity repository, String path,
      HttpServletRequest request) {
    String remoteUrl = repository.getRemoteUrl();
    if (remoteUrl == null || remoteUrl.isBlank()) {
      log.warn("No remote URL configured for proxy repository: {}", repository.getName());
      return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
          .body("No remote URL configured for this proxy repository");
    }

    String repositoryId = String.valueOf(repository.getId());

    // Check cache first
    Optional<CachedContent> cached = getFromCache(repositoryId, path);
    if (cached.isPresent() && !cached.get().isExpired()) {
      log.debug("Serving from cache: repository={}, path={}", repositoryId, path);
      CachedContent content = cached.get();
      return ResponseEntity.ok()
          .contentLength(content.getSize())
          .body(content.getInputStream());
    }

    // Fetch from remote
    String fullUrl = remoteUrl.endsWith("/") ? remoteUrl + path : remoteUrl + "/" + path;
    log.info("Proxying request to remote: {}", fullUrl);
    try {
      byte[] responseBody = restTemplate.getForObject(fullUrl, byte[].class);
      if (responseBody == null) {
        return ResponseEntity.notFound().build();
      }

      // Cache the response
      putToCache(repositoryId, path, new ByteArrayInputStream(responseBody), Duration.ofHours(1));

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      headers.setContentLength(responseBody.length);
      return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
    } catch (Exception e) {
      log.error("Failed to proxy request to {}: {}", fullUrl, e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
          .body("Failed to fetch from remote repository: " + e.getMessage());
    }
  }
}
