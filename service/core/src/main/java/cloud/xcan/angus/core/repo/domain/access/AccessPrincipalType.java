package cloud.xcan.angus.core.repo.domain.access;

import lombok.Getter;

@Getter
public enum AccessPrincipalType {
  USER("user"),
  ROLE("role"),
  GROUP("group"),
  TOKEN("token"),
  API_KEY("api_key");

  private final String value;

  AccessPrincipalType(String value) {
    this.value = value;
  }
}
