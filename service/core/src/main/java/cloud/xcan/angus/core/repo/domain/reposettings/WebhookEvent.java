package cloud.xcan.angus.core.repo.domain.reposettings;

import lombok.Getter;

@Getter
public enum WebhookEvent {
  ARTIFACT_UPLOAD("artifact_upload"),
  ARTIFACT_DOWNLOAD("artifact_download"),
  ARTIFACT_DELETE("artifact_delete"),
  SCAN_COMPLETE("scan_complete"),
  VULNERABILITY_FOUND("vulnerability_found"),
  REPOSITORY_CREATE("repository_create"),
  REPOSITORY_DELETE("repository_delete");

  private final String value;

  WebhookEvent(String value) {
    this.value = value;
  }
}
