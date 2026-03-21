package cloud.xcan.angus.core.gm.application.query.authentication.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.TipMessage.CLIENT_IS_DISABLED_T;
import static cloud.xcan.angus.remote.message.http.Unauthorized.M.INVALID_CLIENT;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthenticationClientQuery;
import cloud.xcan.angus.security.client.CustomOAuth2ClientRepository;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthenticationClientQueryImpl implements AuthenticationClientQuery {

  @Resource
  private CustomOAuth2ClientRepository customOAuth2ClientRepository;

  @Resource
  private PasswordEncoder passwordEncoder;

  /**
   * 根据ID获取详细的OAuth2客户端信息
   */
  @Override
  public CustomOAuth2RegisteredClient detail(String id) {
    return new BizTemplate<CustomOAuth2RegisteredClient>() {

      @Override
      protected CustomOAuth2RegisteredClient process() {
        RegisteredClient client = customOAuth2ClientRepository.findById(id);
        assertResourceNotFound(client, id, "Client");
        return (CustomOAuth2RegisteredClient) client;
      }
    }.execute();
  }

  /**
   * 检索OAuth2客户端列表，支持可选过滤
   */
  @Override
  public List<CustomOAuth2RegisteredClient> list(String id, String clientId, String tenantId) {
    return new BizTemplate<List<CustomOAuth2RegisteredClient>>() {

      @Override
      protected List<CustomOAuth2RegisteredClient> process() {
        List<String> args = new ArrayList<>();
        StringBuilder filter = new StringBuilder(" 1 = 1 ");
        if (isNotEmpty(id)) {
          args.add(id);
          filter.append(" AND id = ").append(id).append(" ");
        }
        if (isNotEmpty(clientId)) {
          args.add(clientId);
          filter.append(" AND client_id = ").append(clientId).append(" ");
        }
        if (isNotEmpty(tenantId)) {
          args.add(tenantId);
          filter.append(" AND tenant_id = ").append(tenantId).append(" ");
        }
        return customOAuth2ClientRepository.findAllBy(filter.toString(),
            args.toArray(new String[0]));
      }
    }.execute();
  }

  /**
   * 验证并根据客户端ID检索OAuth2客户端
   */
  @Override
  public CustomOAuth2RegisteredClient checkAndFind(String clientId) {
    RegisteredClient client = customOAuth2ClientRepository.findByClientId(clientId);
    assertResourceNotFound(client, clientId, "Client");
    return (CustomOAuth2RegisteredClient) client;
  }

  /**
   * 使用客户端密钥验证OAuth2客户端认证
   */
  @Override
  public CustomOAuth2RegisteredClient checkAndFind(String clientId, String clientSecret) {
    CustomOAuth2RegisteredClient client = checkAndFind(clientId);
    assertTrue(passwordEncoder.matches(clientSecret, client.getClientSecret()), INVALID_CLIENT);
    return client;
  }

  /**
   * 使用权限范围验证OAuth2客户端认证
   */
  @Override
  public CustomOAuth2RegisteredClient checkAndFind(String clientId, String clientSecret,
      @Nullable String scope) {
    CustomOAuth2RegisteredClient client = checkAndFind(clientId, clientSecret);
    if (scope != null) {
      Set<String> requestedScopes = new HashSet<>(
          Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
      for (String scope0 : requestedScopes) {
        assertTrue(client.getScopes().contains(scope0),
            String.format("Client scope %s is invalid", scope0));
      }
    }
    return client;
  }

  /**
   * 验证并检索OAuth2客户端，可选择检查启用状态
   */
  @Override
  public CustomOAuth2RegisteredClient checkAndFind(String clientId, boolean checkEnabled) {
    CustomOAuth2RegisteredClient client = checkAndFind(clientId);
    if (checkEnabled) {
      assertTrue(client.isEnabled(), CLIENT_IS_DISABLED_T, new Object[]{client.getClientId()});
    }
    return client;
  }

  /**
   * 根据客户端ID检索有效的OAuth2客户端，不抛出异常
   */
  @Override
  public CustomOAuth2RegisteredClient findValidByClientId0(String clientId) {
    RegisteredClient client = customOAuth2ClientRepository.findByClientId(clientId);
    return nonNull(client) && ((CustomOAuth2RegisteredClient) client).isEnabled()
        ? (CustomOAuth2RegisteredClient) client : null;
  }

}
