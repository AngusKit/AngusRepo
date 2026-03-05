package cloud.xcan.angus.core.repo.domain.repository;

import lombok.Getter;

@Getter
public enum RepositoryStatus {
  ONLINE("online"),
  OFFLINE("offline"),
  ERROR("error"),
  MAINTENANCE("maintenance");

  private final String value;

  RepositoryStatus(String value) {
    this.value = value;
  }
}
