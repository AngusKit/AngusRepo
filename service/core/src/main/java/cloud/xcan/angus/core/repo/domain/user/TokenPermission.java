package cloud.xcan.angus.core.repo.domain.user;

import lombok.Getter;

@Getter
public enum TokenPermission {
  READ("read"),
  WRITE("write"),
  ADMIN("admin");

  private final String value;

  TokenPermission(String value) {
    this.value = value;
  }
}
