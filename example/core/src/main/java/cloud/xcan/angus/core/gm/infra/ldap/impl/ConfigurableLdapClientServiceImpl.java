package cloud.xcan.angus.core.gm.infra.ldap.impl;

import cloud.xcan.angus.core.gm.infra.ldap.LdapClientService;
import cloud.xcan.angus.core.gm.infra.ldap.LdapConnectionConfig;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.RootDSE;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLSocketFactory;

/**
 * 可配置的LDAP客户端服务实现
 */
public class ConfigurableLdapClientServiceImpl implements LdapClientService {

  private final LdapConnectionConfig config;

  public ConfigurableLdapClientServiceImpl(LdapConnectionConfig config) {
    this.config = config;
  }

  /**
   * 创建LDAP连接
   */
  private LDAPConnection createConnection() throws LDAPException {
    // 创建连接选项并设置超时
    LDAPConnectionOptions options = new LDAPConnectionOptions();
    options.setConnectTimeoutMillis(config.getConnectionTimeout());
    options.setResponseTimeoutMillis(config.getResponseTimeout());

    LDAPConnection connection;
    if (config.isUseSsl()) {
      try {
        SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());
        SSLSocketFactory socketFactory = sslUtil.createSSLSocketFactory();
        connection = new LDAPConnection(socketFactory, options, config.getHost(), config.getPort());
      } catch (Exception e) {
        throw new LDAPException(ResultCode.CONNECT_ERROR, "无法创建SSL连接: " + e.getMessage(), e);
      }
    } else {
      connection = new LDAPConnection(options, config.getHost(), config.getPort());
    }

    // 如果配置了绑定DN和密码，进行绑定
    if (config.getBindDn() != null && !config.getBindDn().isEmpty()
        && config.getBindPassword() != null) {
      connection.bind(config.getBindDn(), config.getBindPassword());
    }

    return connection;
  }

  /**
   * 将LDAP Entry转换为LdapEntry
   */
  private LdapEntry convertToLdapEntry(SearchResultEntry entry) {
    LdapEntry ldapEntry = new LdapEntry();
    ldapEntry.setDn(entry.getDN());
    Map<String, List<String>> attributes = new HashMap<>();
    for (Attribute attr : entry.getAttributes()) {
      String attrName = attr.getName();
      List<String> values = new ArrayList<>();
      for (String value : attr.getValues()) {
        values.add(value);
      }
      attributes.put(attrName, values);
    }
    ldapEntry.setAttributes(attributes);
    return ldapEntry;
  }

  @Override
  public LdapConnectionResult testConnection() {
    LdapConnectionResult result = new LdapConnectionResult();
    long startTime = System.currentTimeMillis();

    try (LDAPConnection connection = createConnection()) {
      long endTime = System.currentTimeMillis();
      result.setConnected(true);
      result.setResponseTime(endTime - startTime);

      // 获取RootDSE信息
      RootDSE rootDSE = connection.getRootDSE();
      if (rootDSE != null) {
        String version = rootDSE.getAttributeValue("supportedLDAPVersion");
        if (version != null) {
          result.setVersion(version);
        }
        String vendor = rootDSE.getAttributeValue("vendorName");
        if (vendor == null) {
          // 尝试从其他属性推断
          String namingContexts = rootDSE.getAttributeValue("namingContexts");
          if (namingContexts != null && namingContexts.contains("dc=domain")) {
            vendor = "Active Directory";
          } else {
            // 尝试从subschemaSubentry推断
            String subschemaSubentry = rootDSE.getAttributeValue("subschemaSubentry");
            if (subschemaSubentry != null && subschemaSubentry.contains("cn=ActiveDirectory")) {
              vendor = "Active Directory";
            } else {
              vendor = "LDAP Server";
            }
          }
        }
        result.setVendor(vendor);
      }
    } catch (LDAPException e) {
      result.setConnected(false);
      result.setErrorMessage(e.getMessage());
      result.setResponseTime(System.currentTimeMillis() - startTime);
    } catch (Exception e) {
      result.setConnected(false);
      result.setErrorMessage("连接失败: " + e.getMessage());
      result.setResponseTime(System.currentTimeMillis() - startTime);
    }

    return result;
  }

  @Override
  public LdapAuthResult authenticate(String username, String password) {
    LdapAuthResult result = new LdapAuthResult();

    try (LDAPConnection connection = createConnection()) {
      // 搜索用户DN
      String userDN = searchUserDN(connection, username);
      if (userDN == null) {
        result.setAuthenticated(false);
        result.setErrorMessage("用户不存在");
        return result;
      }

      // 使用用户DN和密码进行绑定认证
      BindResult bindResult = connection.bind(userDN, password);
      if (bindResult.getResultCode() == ResultCode.SUCCESS) {
        result.setAuthenticated(true);
        result.setUserDN(userDN);

        // 查询用户属性
        String[] attributes = {"cn", "mail", "uid", "displayName", "sn", "givenName", "mobile",
            "telephoneNumber", "department", "title", "description"};
        com.unboundid.ldap.sdk.Entry userEntry = connection.getEntry(userDN, attributes);
        if (userEntry != null) {
          Map<String, String> attrMap = new HashMap<>();
          for (Attribute attr : userEntry.getAttributes()) {
            String value = attr.getValue();
            if (value != null) {
              attrMap.put(attr.getName(), value);
            }
          }
          result.setAttributes(attrMap);
        }
      } else {
        result.setAuthenticated(false);
        result.setErrorMessage("认证失败: " + bindResult.getResultCode());
      }
    } catch (LDAPException e) {
      result.setAuthenticated(false);
      result.setErrorMessage("认证失败: " + e.getMessage());
    } catch (Exception e) {
      result.setAuthenticated(false);
      result.setErrorMessage("认证失败: " + e.getMessage());
    }

    return result;
  }

  /**
   * 搜索用户DN 注意：部分LDAP服务器（如AD）默认返回结果数限制较严，可能抛出 SIZE_LIMIT_EXCEEDED， 但通常已返回匹配的条目，此时从异常中提取第一个结果即可
   */
  private String searchUserDN(LDAPConnection connection, String username) throws LDAPException {
    String baseDN = config.getBaseDn();
    String filterStr = config.getUserFilter();
    if (filterStr == null || filterStr.isEmpty()) {
      filterStr = "(uid=" + Filter.encodeValue(username) + ")";
    } else if (filterStr.contains("{0}")) {
      // 有占位符时替换
      filterStr = filterStr.replace("{0}", Filter.encodeValue(username));
    } else {
      // 无占位符时追加 uid 条件：(&(原filter)(uid=username))
      filterStr = "(&" + filterStr + "(uid=" + Filter.encodeValue(username) + "))";
    }

    Filter filter = Filter.create(filterStr);
    SearchRequest searchRequest = new SearchRequest(baseDN, SearchScope.SUB, filter, "dn");
    searchRequest.setSizeLimit(1);

    try {
      SearchResult searchResult = connection.search(searchRequest);
      if (searchResult.getEntryCount() > 0) {
        return searchResult.getSearchEntries().get(0).getDN();
      }
      return null;
    } catch (LDAPException e) {
      // 服务器返回 size limit exceeded 时，通常已返回至少1条匹配结果，可直接使用
      if (e.getResultCode() == ResultCode.SIZE_LIMIT_EXCEEDED && e instanceof LDAPSearchException) {
        SearchResult searchResult = ((LDAPSearchException) e).getSearchResult();
        if (searchResult != null && searchResult.getEntryCount() > 0) {
          return searchResult.getSearchEntries().get(0).getDN();
        }
      }
      throw e;
    }
  }

  @Override
  public List<LdapClientService.LdapEntry> searchUsers(String baseDN, String filter,
      String[] attributes, Integer limit) {
    List<LdapClientService.LdapEntry> entries = new ArrayList<>();

    try (LDAPConnection connection = createConnection()) {
      String searchBaseDN = baseDN != null && !baseDN.isEmpty() ? baseDN : config.getBaseDn();
      String searchFilter = filter != null && !filter.isEmpty() ? filter
          : (config.getUserFilter() != null && !config.getUserFilter().isEmpty()
              ? config.getUserFilter()
              : "(objectClass=person)");

      Filter ldapFilter = Filter.create(searchFilter);
      SearchRequest searchRequest = new SearchRequest(searchBaseDN, SearchScope.SUB, ldapFilter,
          attributes != null ? attributes : new String[]{"*"});

      if (limit != null && limit > 0) {
        searchRequest.setSizeLimit(limit);
      }

      SearchResult searchResult = connection.search(searchRequest);
      for (SearchResultEntry entry : searchResult.getSearchEntries()) {
        entries.add(convertToLdapEntry(entry));
      }
    } catch (LDAPException e) {
      // 记录错误但不抛出异常，返回已找到的条目
      // 可以在这里添加日志记录
    } catch (Exception e) {
      // 记录错误但不抛出异常
    }

    return entries;
  }

  @Override
  public List<LdapClientService.LdapEntry> searchGroups(String baseDN, String filter,
      String[] attributes) {
    List<LdapClientService.LdapEntry> entries = new ArrayList<>();

    try (LDAPConnection connection = createConnection()) {
      String searchBaseDN = baseDN != null && !baseDN.isEmpty() ? baseDN : config.getBaseDn();
      String searchFilter = filter != null && !filter.isEmpty() ? filter
          : (config.getGroupFilter() != null && !config.getGroupFilter().isEmpty()
              ? config.getGroupFilter()
              : "(objectClass=groupOfNames)");

      Filter ldapFilter = Filter.create(searchFilter);
      SearchRequest searchRequest = new SearchRequest(searchBaseDN, SearchScope.SUB, ldapFilter,
          attributes != null ? attributes : new String[]{"*"});

      SearchResult searchResult = connection.search(searchRequest);
      for (SearchResultEntry entry : searchResult.getSearchEntries()) {
        entries.add(convertToLdapEntry(entry));
      }
    } catch (LDAPException e) {
      // 记录错误但不抛出异常
    } catch (Exception e) {
      // 记录错误但不抛出异常
    }

    return entries;
  }

  @Override
  public LdapClientService.LdapEntry getEntry(String dn, String[] attributes) {
    try (LDAPConnection connection = createConnection()) {
      com.unboundid.ldap.sdk.Entry entry = connection.getEntry(dn,
          attributes != null ? attributes : new String[]{"*"});
      if (entry != null) {
        // 将com.unboundid.ldap.sdk.Entry转换为SearchResultEntry格式
        SearchResultEntry searchEntry = new SearchResultEntry(entry.getDN(), entry.getAttributes());
        return convertToLdapEntry(searchEntry);
      }
    } catch (LDAPException e) {
      // 记录错误但不抛出异常
    } catch (Exception e) {
      // 记录错误但不抛出异常
    }

    return null;
  }

  @Override
  public List<LdapClientService.LdapEntry> getGroupMembers(String groupDN, String[] attributes) {
    List<LdapClientService.LdapEntry> members = new ArrayList<>();

    try (LDAPConnection connection = createConnection()) {
      // 获取组的member属性
      com.unboundid.ldap.sdk.Entry group = connection.getEntry(groupDN, new String[]{"member"});
      if (group == null) {
        return members;
      }

      Attribute memberAttr = group.getAttribute("member");
      if (memberAttr == null) {
        // 尝试其他可能的属性名
        memberAttr = group.getAttribute("memberUid");
        if (memberAttr == null) {
          memberAttr = group.getAttribute("uniqueMember");
        }
      }

      if (memberAttr != null) {
        String[] memberDNs = memberAttr.getValues();
        String[] searchAttributes = attributes != null ? attributes : new String[]{"*"};

        for (String memberDN : memberDNs) {
          try {
            com.unboundid.ldap.sdk.Entry member = connection.getEntry(memberDN, searchAttributes);
            if (member != null) {
              // 将com.unboundid.ldap.sdk.Entry转换为SearchResultEntry格式
              SearchResultEntry searchMember = new SearchResultEntry(member.getDN(),
                  member.getAttributes());
              members.add(convertToLdapEntry(searchMember));
            }
          } catch (LDAPException e) {
            // 忽略单个成员获取失败，继续处理其他成员
          }
        }
      }
    } catch (LDAPException e) {
      // 记录错误但不抛出异常
    } catch (Exception e) {
      // 记录错误但不抛出异常
    }

    return members;
  }
}
