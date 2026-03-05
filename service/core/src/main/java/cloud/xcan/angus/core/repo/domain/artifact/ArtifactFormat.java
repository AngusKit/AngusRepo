package cloud.xcan.angus.core.repo.domain.artifact;

import lombok.Getter;

@Getter
public enum ArtifactFormat {
  MAVEN("maven"),
  DOCKER("docker"),
  NPM("npm"),
  PYPI("pypi"),
  NUGET("nuget"),
  APT("apt"),
  YUM("yum"),
  RAW("raw");

  private final String value;

  ArtifactFormat(String value) {
    this.value = value;
  }
}
