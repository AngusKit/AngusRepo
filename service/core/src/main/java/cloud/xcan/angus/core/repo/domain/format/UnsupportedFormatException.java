package cloud.xcan.angus.core.repo.domain.format;

public class UnsupportedFormatException extends RuntimeException {

  public UnsupportedFormatException(String message) {
    super(message);
  }

  public UnsupportedFormatException(String message, Throwable cause) {
    super(message, cause);
  }
}
