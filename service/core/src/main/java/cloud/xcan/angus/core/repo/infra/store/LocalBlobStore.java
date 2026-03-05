package cloud.xcan.angus.core.repo.infra.store;

import cloud.xcan.angus.core.repo.domain.format.store.BlobStorageException;
import cloud.xcan.angus.core.repo.domain.format.store.BlobStore;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Local filesystem-based implementation of {@link BlobStore}.
 *
 * <p>Stores blobs on the local filesystem using a configurable base directory.
 * Files are organized by tenant and repository: {@code basePath/tenantId/repositoryId/path}.
 */
@Slf4j
@Component
public class LocalBlobStore implements BlobStore {

  private final Path basePath;

  public LocalBlobStore(
      @Value("${angus.repo.storage.base-dir:${user.home}/.angusrepo/storage}") String baseDir) {
    this.basePath = Paths.get(baseDir);
    log.info("Initialized LocalBlobStore with base directory: {}", this.basePath);
  }

  @Override
  public String store(String tenantId, String repositoryId, String path, InputStream data) {
    Path targetPath = resolvePath(tenantId, repositoryId, path);
    log.debug("Storing blob at: {}", targetPath);
    try {
      Files.createDirectories(targetPath.getParent());
      Files.copy(data, targetPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new BlobStorageException("Failed to store blob at: " + targetPath, e);
    }
    return path;
  }

  @Override
  public InputStream retrieve(String tenantId, String repositoryId, String path) {
    Path targetPath = resolvePath(tenantId, repositoryId, path);
    log.debug("Retrieving blob from: {}", targetPath);
    try {
      return Files.newInputStream(targetPath);
    } catch (IOException e) {
      throw new BlobStorageException("Failed to retrieve blob from: " + targetPath, e);
    }
  }

  @Override
  public void delete(String tenantId, String repositoryId, String path) {
    Path targetPath = resolvePath(tenantId, repositoryId, path);
    log.debug("Deleting blob at: {}", targetPath);
    try {
      Files.deleteIfExists(targetPath);
    } catch (IOException e) {
      throw new BlobStorageException("Failed to delete blob at: " + targetPath, e);
    }
  }

  @Override
  public boolean exists(String tenantId, String repositoryId, String path) {
    Path targetPath = resolvePath(tenantId, repositoryId, path);
    return Files.exists(targetPath);
  }

  @Override
  public long size(String tenantId, String repositoryId, String path) {
    Path targetPath = resolvePath(tenantId, repositoryId, path);
    try {
      return Files.size(targetPath);
    } catch (IOException e) {
      throw new BlobStorageException("Failed to get size of blob at: " + targetPath, e);
    }
  }

  /**
   * Resolve the full filesystem path for a blob.
   */
  private Path resolvePath(String tenantId, String repositoryId, String path) {
    return basePath.resolve(tenantId).resolve(repositoryId).resolve(path);
  }
}
