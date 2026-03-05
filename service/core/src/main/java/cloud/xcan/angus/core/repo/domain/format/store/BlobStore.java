package cloud.xcan.angus.core.repo.domain.format.store;

import java.io.InputStream;

/**
 * Blob storage abstraction layer.
 * Provides a unified interface for artifact storage operations across all formats.
 */
public interface BlobStore {

  /**
   * Store a blob.
   *
   * @return the storage path
   */
  String store(String tenantId, String repositoryId, String path, InputStream data);

  /**
   * Retrieve a blob.
   */
  InputStream retrieve(String tenantId, String repositoryId, String path);

  /**
   * Delete a blob.
   */
  void delete(String tenantId, String repositoryId, String path);

  /**
   * Check if a blob exists.
   */
  boolean exists(String tenantId, String repositoryId, String path);

  /**
   * Get the size of a blob.
   */
  long size(String tenantId, String repositoryId, String path);
}
