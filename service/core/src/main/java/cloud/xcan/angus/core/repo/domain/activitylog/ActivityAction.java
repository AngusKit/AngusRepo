package cloud.xcan.angus.core.repo.domain.activitylog;

import lombok.Getter;

/**
 * 活动操作类型枚举
 */
@Getter
public enum ActivityAction {
  UPLOAD("upload"),
  DOWNLOAD("download"),
  DELETE("delete"),
  UPDATE("update"),
  CREATE("create"),
  USER_ADD("user_add"),
  USER_REMOVE("user_remove"),
  ROLE_CHANGE("role_change"),
  SCAN("scan"),
  ACCESS_GRANT("access_grant"),
  ACCESS_REVOKE("access_revoke"),
  CLEANUP("cleanup"),
  SETTINGS_UPDATE("settings_update");

  private final String value;

  ActivityAction(String value) {
    this.value = value;
  }
}
