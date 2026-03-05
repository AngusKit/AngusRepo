package cloud.xcan.angus.core.repo.domain.access;

import lombok.Getter;

@Getter
public enum AccessPermission {
  READ("read"),
  WRITE("write"),
  DELETE("delete"),
  ADMIN("admin");

  private final String value;

  AccessPermission(String value) {
    this.value = value;
  }
}
