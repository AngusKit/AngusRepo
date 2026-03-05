package cloud.xcan.angus.core.repo.domain.system;

import lombok.Getter;

@Getter
public enum ConnectionType {
  LDAP("ldap"),
  SAML("saml"),
  S3("s3"),
  SLACK("slack"),
  SMTP("smtp");

  private final String value;

  ConnectionType(String value) {
    this.value = value;
  }
}
