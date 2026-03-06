package cloud.xcan.angus.core.repo.domain.format.store;

/**
 * Exception thrown when blob storage operations fail.
 */
public class BlobStorageException extends RuntimeException {

  public BlobStorageException(String message) {
    super(message);
  }

  public BlobStorageException(String message, Throwable cause) {
    super(message, cause);
  }
}
