package cloud.xcan.angus.core.gm.infra.ldap;

import cloud.xcan.angus.core.gm.domain.ldap.Ldap;
import cloud.xcan.angus.core.gm.infra.ldap.impl.ConfigurableLdapClientServiceImpl;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapConnectionTestDto;
import cloud.xcan.angus.remote.message.ProtocolException;
import org.springframework.stereotype.Component;

/**
 * LDAP客户端服务工厂
 */
@Component
public class LdapClientServiceFactory {

  /**
   * 根据LDAP配置创建客户端服务
   */
  public LdapClientService create(Ldap config) {
    if (config == null) {
      throw ProtocolException.of("LDAP配置不能为空");
    }

    // 解析serverUrl
    LdapConnectionConfig connectionConfig = LdapConnectionConfig.fromServerUrl(
        config.getServerUrl(), false);

    // 设置连接配置
    connectionConfig.setBindDn(config.getBindDn());
    connectionConfig.setBindPassword(config.getBindPassword());
    connectionConfig.setBaseDn(config.getBaseDn());
    connectionConfig.setUserFilter(config.getUserFilter());
    connectionConfig.setGroupFilter(config.getGroupFilter());

    // 创建配置好的客户端服务实例
    return new ConfigurableLdapClientServiceImpl(connectionConfig);
  }

  /**
   * 根据连接测试DTO创建客户端服务
   */
  public LdapClientService create(LdapConnectionTestDto dto) {
    if (dto == null) {
      throw ProtocolException.of("LDAP连接测试DTO不能为空");
    }

    // 构建serverUrl
    String serverUrl = dto.getServer();
    if (!serverUrl.contains(":")) {
      // 如果没有端口，添加默认端口
      int defaultPort = Boolean.TRUE.equals(dto.getUseSsl()) ? 636 : 389;
      serverUrl = serverUrl + ":" + defaultPort;
    }

    // 解析serverUrl
    LdapConnectionConfig connectionConfig = LdapConnectionConfig.fromServerUrl(serverUrl,
        Boolean.TRUE.equals(dto.getUseSsl()));

    // 设置连接配置
    connectionConfig.setBindDn(dto.getBindDN());
    connectionConfig.setBindPassword(dto.getBindPassword());
    connectionConfig.setBaseDn(dto.getBaseDN());

    // 创建配置好的客户端服务实例
    return new ConfigurableLdapClientServiceImpl(connectionConfig);
  }
}
