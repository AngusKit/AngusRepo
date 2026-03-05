package cloud.xcan.angus.core.repo.domain.repository;

import lombok.Getter;

@Getter
public enum RepositoryType {
  HOSTED("hosted"),
  PROXY("proxy"),
  GROUP("group"),
  VIRTUAL("virtual");

  private final String value;

  RepositoryType(String value) {
    this.value = value;
  }
}
