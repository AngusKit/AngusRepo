package cloud.xcan.angus.core.repo.domain.activitylog;

import lombok.Getter;

/**
 * 活动分类枚举
 */
@Getter
public enum ActivityCategory {
  ARTIFACT("artifact"),
  REPOSITORY("repository"),
  USER("user"),
  SECURITY("security"),
  SYSTEM("system");

  private final String value;

  ActivityCategory(String value) {
    this.value = value;
  }
}
