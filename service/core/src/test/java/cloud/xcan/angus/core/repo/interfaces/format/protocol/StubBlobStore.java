package cloud.xcan.angus.core.repo.interfaces.format.protocol;

import cloud.xcan.angus.core.repo.domain.format.store.BlobStore;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stub implementation of {@link BlobStore} for unit testing protocol controllers.
 * Stores blobs in-memory using a ConcurrentHashMap keyed by composite path.
 */
class StubBlobStore implements BlobStore {

  private final ConcurrentHashMap<String, byte[]> storage = new ConcurrentHashMap<>();

  void addBlob(String tenantId, String repositoryId, String path, String content) {
    storage.put(key(tenantId, repositoryId, path), content.getBytes(StandardCharsets.UTF_8));
  }

  void addBlob(String tenantId, String repositoryId, String path, byte[] content) {
    storage.put(key(tenantId, repositoryId, path), content);
  }

  void clear() {
    storage.clear();
  }

  @Override
  public String store(String tenantId, String repositoryId, String path, InputStream data) {
    try {
      byte[] bytes = data.readAllBytes();
      storage.put(key(tenantId, repositoryId, path), bytes);
      return path;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public InputStream retrieve(String tenantId, String repositoryId, String path) {
    byte[] data = storage.get(key(tenantId, repositoryId, path));
    if (data == null) {
      return null;
    }
    return new ByteArrayInputStream(data);
  }

  @Override
  public void delete(String tenantId, String repositoryId, String path) {
    storage.remove(key(tenantId, repositoryId, path));
  }

  @Override
  public boolean exists(String tenantId, String repositoryId, String path) {
    return storage.containsKey(key(tenantId, repositoryId, path));
  }

  @Override
  public long size(String tenantId, String repositoryId, String path) {
    byte[] data = storage.get(key(tenantId, repositoryId, path));
    return data != null ? data.length : 0;
  }

  private String key(String tenantId, String repositoryId, String path) {
    return tenantId + "/" + repositoryId + "/" + path;
  }
}
