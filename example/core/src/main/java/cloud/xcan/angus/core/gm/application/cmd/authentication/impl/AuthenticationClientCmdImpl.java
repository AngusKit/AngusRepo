package cloud.xcan.angus.core.gm.application.cmd.authentication.impl;

import static cloud.xcan.angus.api.commonlink.GMConstant.TENANT_OAUTH2_CLIENT_ID;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceExisted;
import static cloud.xcan.angus.core.gm.application.converter.AuthorizationClientConverter.getSystemTokenClientId;
import static cloud.xcan.angus.core.utils.CoreUtils.copyProperties;
import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreNull;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.client.enums.ClientSource;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.authentication.AuthenticationClientCmd;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthenticationClientQuery;
import cloud.xcan.angus.security.authentication.service.JdbcOAuth2AuthorizationService;
import cloud.xcan.angus.security.client.CustomOAuth2ClientRepository;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ConfigurationSettingNames;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthenticationClientCmdImpl implements AuthenticationClientCmd {

  @Resource
  private AuthenticationClientQuery authenticationClientQuery;

  @Resource
  private CustomOAuth2ClientRepository customOAuth2ClientRepository;

  @Resource
  private JdbcOAuth2AuthorizationService oauth2AuthorizationService;

  /**
   * 创建新的OAuth2客户端
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<String, Object> add(CustomOAuth2RegisteredClient client) {
    return new BizTemplate<IdKey<String, Object>>() {
      @Override
      protected void checkParams() {
        // 验证客户端不存在
        RegisteredClient clientDb = customOAuth2ClientRepository.findByClientId(
            client.getClientId());
        assertResourceExisted(clientDb, client.getClientId(), "Client");
      }

      @Override
      protected IdKey<String, Object> process() {
        // 保存客户端到仓库
        customOAuth2ClientRepository.save(client);
        return IdKey.of(client.getId(), client.getClientId());
      }
    }.execute();
  }

  /**
   * 使用新配置更新现有的OAuth2客户端
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void update(CustomOAuth2RegisteredClient client) {
    new BizTemplate<Void>() {
      CustomOAuth2RegisteredClient clientDb;

      @Override
      protected void checkParams() {
        // 验证客户端存在
        clientDb = authenticationClientQuery.detail(client.getId());
      }

      @Override
      protected Void process() {
        // 使用空值安全的属性复制更新客户端
        customOAuth2ClientRepository.save(copyPropertiesIgnoreNull(client, clientDb));
        return null;
      }
    }.execute();
  }

  /**
   * 通过创建新客户端或更新现有客户端来替换OAuth2客户端
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public IdKey<String, Object> replace(CustomOAuth2RegisteredClient client) {
    return new BizTemplate<IdKey<String, Object>>() {
      CustomOAuth2RegisteredClient clientDb;

      @Override
      protected void checkParams() {
        if (nonNull(client.getClientId())) {
          // 如果更新，验证客户端存在
          clientDb = authenticationClientQuery.detail(client.getId());
        }
      }

      @Override
      protected IdKey<String, Object> process() {
        if (isNull(client.getId())) {
          return add(client);
        }

        // 更新客户端，同时保留不可变字段
        customOAuth2ClientRepository.save(copyProperties(client, clientDb, false,
            "clientId", "clientIdIssuedAt", "platform", "source", "tenantId", "tenantName",
            "createdBy", "createdDate"));

        return IdKey.of(clientDb.getId(), clientDb.getClientId());
      }
    }.execute();
  }

  /**
   * 删除OAuth2客户端并清理相关授权数据
   */
  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(HashSet<String> clientIds) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        for (String clientId : clientIds) {
          // 从仓库中删除客户端
          customOAuth2ClientRepository.deleteByClientId(clientId);
          // 清理授权服务数据
          oauth2AuthorizationService.removeByClientId(clientId);
        }
        return null;
      }
    }.execute();
  }

  /**
   * 根据名称和来源删除系统令牌客户端
   */
  @Override
  public void deleteSystemTokenClient(String tokenName, ClientSource source) {
    customOAuth2ClientRepository.deleteByClientId(getSystemTokenClientId(tokenName, source));
  }

  @Override
  public void updateAccessTokenTimeToLive(int accessTokenTimeInSeconds) {
    CustomOAuth2RegisteredClient clientDb = authenticationClientQuery.detail(
        TENANT_OAUTH2_CLIENT_ID);
    Map<String, Object> settings = clientDb.getTokenSettings().getSettings();
    settings.put(ConfigurationSettingNames.Token.ACCESS_TOKEN_TIME_TO_LIVE,
        accessTokenTimeInSeconds);
    TokenSettings tokenSettings = TokenSettings.withSettings(settings).build();
    clientDb.setTokenSettings(tokenSettings);
    customOAuth2ClientRepository.save(clientDb);
  }

}
