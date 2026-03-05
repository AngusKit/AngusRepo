package cloud.xcan.angus.core.repo.domain.team;

import lombok.Getter;

@Getter
public enum UserRole {
  OWNER("owner"),
  ADMIN("admin"),
  DEVELOPER("developer"),
  VIEWER("viewer");

  private final String value;

  UserRole(String value) {
    this.value = value;
  }
}
