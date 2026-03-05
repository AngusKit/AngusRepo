package cloud.xcan.angus.core.repo.domain.upload;

import lombok.Getter;

@Getter
public enum UploadStatus {
  PENDING("pending"),
  UPLOADING("uploading"),
  PROCESSING("processing"),
  COMPLETED("completed"),
  FAILED("failed"),
  CANCELLED("cancelled");

  private final String value;

  UploadStatus(String value) {
    this.value = value;
  }

  public boolean isActive() {
    return this == PENDING || this == UPLOADING || this == PROCESSING;
  }

  public boolean isTerminal() {
    return this == COMPLETED || this == FAILED || this == CANCELLED;
  }
}
