package cloud.xcan.angus.core.gm.infra.ldap;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * LDAP客户端服务接口
 */
public interface LdapClientService {

  /**
   * 测试LDAP连接
   */
  LdapConnectionResult testConnection();

  /**
   * 测试LDAP认证
   */
  LdapAuthResult authenticate(String username, String password);

  /**
   * 搜索LDAP用户
   */
  List<LdapEntry> searchUsers(String baseDN, String filter, String[] attributes, Integer limit);

  /**
   * 搜索LDAP组
   */
  List<LdapEntry> searchGroups(String baseDN, String filter, String[] attributes);

  /**
   * 获取LDAP条目
   */
  LdapEntry getEntry(String dn, String[] attributes);

  /**
   * 获取组的成员列表
   */
  List<LdapEntry> getGroupMembers(String groupDN, String[] attributes);

  /**
   * LDAP连接结果
   */
  @Setter
  @Getter
  class LdapConnectionResult {

    private boolean connected;
    private Long responseTime;
    private String version;
    private String vendor;
    private String errorMessage;

  }

  /**
   * LDAP认证结果
   */
  @Setter
  @Getter
  class LdapAuthResult {

    private boolean authenticated;
    private String userDN;
    private Map<String, String> attributes;
    private String errorMessage;

  }

  /**
   * LDAP条目
   */
  @Setter
  @Getter
  class LdapEntry {

    private String dn;
    private Map<String, List<String>> attributes;

    public String getAttributeValue(String name) {
      if (attributes == null || !attributes.containsKey(name)) {
        return null;
      }
      List<String> values = attributes.get(name);
      return values != null && !values.isEmpty() ? values.get(0) : null;
    }

    public List<String> getAttributeValues(String name) {
      return attributes != null ? attributes.get(name) : null;
    }
  }
}
