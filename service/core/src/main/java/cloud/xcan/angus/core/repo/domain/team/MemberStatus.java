package cloud.xcan.angus.core.repo.domain.team;

import lombok.Getter;

@Getter
public enum MemberStatus {
  ACTIVE("active"),
  INACTIVE("inactive"),
  SUSPENDED("suspended");

  private final String value;

  MemberStatus(String value) {
    this.value = value;
  }
}
