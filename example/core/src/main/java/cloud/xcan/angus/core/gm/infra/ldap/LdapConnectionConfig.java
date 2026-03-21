package cloud.xcan.angus.core.gm.infra.ldap;

import lombok.Getter;
import lombok.Setter;

/**
 * LDAP连接配置
 */
@Getter
@Setter
public class LdapConnectionConfig {

  private String host;
  private int port;
  private boolean useSsl;
  private String bindDn;
  private String bindPassword;
  private String baseDn;
  private String userFilter;
  private String groupFilter;
  private int connectionTimeout = 5000; // 默认5秒
  private int responseTimeout = 30000; // 默认30秒

  /**
   * 解析serverUrl，支持格式： - host:port - ldap://host:port - ldaps://host:port
   */
  public static LdapConnectionConfig fromServerUrl(String serverUrl, boolean useSsl) {
    LdapConnectionConfig config = new LdapConnectionConfig();

    String url = serverUrl.trim();
    String host;
    int port;
    boolean ssl = useSsl;

    // 移除协议前缀，并确定是否使用SSL
    if (url.startsWith("ldap://")) {
      url = url.substring(7);
      ssl = false;
    } else if (url.startsWith("ldaps://")) {
      url = url.substring(8);
      ssl = true;
    }

    config.setUseSsl(ssl);

    // 解析host和port
    int colonIndex = url.indexOf(':');
    if (colonIndex > 0) {
      host = url.substring(0, colonIndex);
      try {
        port = Integer.parseInt(url.substring(colonIndex + 1));
      } catch (NumberFormatException e) {
        port = ssl ? 636 : 389;
      }
    } else {
      host = url;
      port = ssl ? 636 : 389;
    }

    config.setHost(host);
    config.setPort(port);
    return config;
  }
}
