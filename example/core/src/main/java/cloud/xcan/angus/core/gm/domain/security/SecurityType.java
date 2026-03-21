package cloud.xcan.angus.core.gm.domain.security;

import cloud.xcan.angus.spec.experimental.Value;

public enum SecurityType implements Value<String> {

  PASSWORD_POLICY,

  LOGIN_SECURITY,

  IP_WHITELIST,

  SESSION_CONFIG,

  EUREKA_CONFIG,

  SESSION,

  API_SECURITY,

  DATA_ENCRYPTION,

  ACCESS_CONTROL,

  QUOTA_ALERT_RULES,

  LDAP_FIELD_MAPPING,

  NOTIFICATION_CONFIG;

  @Override
  public String getValue() {
    return this.name();
  }
}
