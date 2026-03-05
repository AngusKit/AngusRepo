package cloud.xcan.angus.core.repo.domain.repository;

import lombok.Getter;

@Getter
public enum RepositoryFormat {
  MAVEN("maven"),
  DOCKER("docker"),
  NPM("npm"),
  PYPI("pypi"),
  NUGET("nuget"),
  APT("apt"),
  YUM("yum"),
  HELM("helm"),
  GO("go"),
  RAW("raw");

  private final String value;

  RepositoryFormat(String value) {
    this.value = value;
  }
}
