package cloud.xcan.angus.core.repo.domain.system;

import lombok.Getter;

@Getter
public enum StorageBackend {
  LOCAL("local"),
  S3("s3"),
  AZURE("azure"),
  GCS("gcs");

  private final String value;

  StorageBackend(String value) {
    this.value = value;
  }
}
