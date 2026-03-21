package cloud.xcan.angus.core.gm.application.cmd.authentication.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.converter.AuthorizationClientSignConverter.privateSignupToDomain;
import static cloud.xcan.angus.core.gm.infra.authentication.OAuth2Utils.submitOauth2ClientSignInRequest;
import static java.util.Objects.nonNull;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS;

import cloud.xcan.angus.api.commonlink.GMConstant;
import cloud.xcan.angus.api.commonlink.client.ClientAuth;
import cloud.xcan.angus.api.commonlink.client.enums.Client2pSignupBiz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.authentication.AuthenticationClientSignCmd;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthenticationClientQuery;
import cloud.xcan.angus.remote.message.SysException;
import cloud.xcan.angus.security.client.CustomOAuth2ClientRepository;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import jakarta.annotation.Resource;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationClientSignCmdImpl implements AuthenticationClientSignCmd {

  @Resource
  private AuthenticationClientQuery authClientQuery;

  @Resource
  private CustomOAuth2ClientRepository customOAuth2ClientRepository;

  @Resource
  private PasswordEncoder passwordEncoder;

  /**
   * 使用客户端凭证授权类型对OAuth2客户端进行认证
   */
  @Override
  public Map<String, String> signin(String clientId, String clientSecret, String scope) {
    return new BizTemplate<Map<String, String>>(false) {
      CustomOAuth2RegisteredClient clientDb;

      @Override
      protected void checkParams() {
        // 验证客户端凭证和权限范围
        clientDb = authClientQuery.checkAndFind(clientId, clientSecret, scope);
        // 确保客户端支持客户端凭证授权类型
        assertTrue(clientDb.getAuthorizationGrantTypes().contains(CLIENT_CREDENTIALS),
            "Unsupported client credentials grant type");
      }

      @Override
      protected Map<String, String> process() {
        // 提交OAuth2客户端认证请求
        try {
          return submitOauth2ClientSignInRequest(clientId, clientSecret, scope);
        } catch (Throwable e) {
          String cause = nonNull(e.getCause()) ? e.getCause().getMessage() : e.getMessage();
          log.error(cause, e);
          throw new SysException(cause);
        }
      }
    }.execute();
  }

  /**
   * 为业务操作注册私有OAuth2客户端
   */
  @Override
  public ClientAuth signupByDoor(Client2pSignupBiz signupBiz, Long tenantId, String tenantName,
      Long resourceId) {
    return new BizTemplate<ClientAuth>() {
      @Override
      protected ClientAuth process() {
        // 根据业务参数生成客户端ID
        String clientId = String.format(GMConstant.SIGN2P_CLIENT_ID_FMT, tenantId,
            signupBiz.name().toLowerCase(), resourceId);
        CustomOAuth2RegisteredClient clientDb = authClientQuery.findValidByClientId0(clientId);

        if (clientDb != null) {
          // 续期访问授权并更新客户端认证信息
          String clientSecret = UUID.randomUUID().toString();
          clientDb.setClientSecret(passwordEncoder.encode(clientSecret));
          customOAuth2ClientRepository.save(clientDb);
          log.info("Re-acquire biz[{}-{}] client[{}] access authorization information, "
              + "client secret is updated", signupBiz.name(), resourceId, clientId);
          return new ClientAuth().setClientId(clientId).setClientSecret(clientSecret);
        }

        // 创建新的私有客户端
        CustomOAuth2RegisteredClient client = privateSignupToDomain(
            clientId, signupBiz, tenantId, tenantName, resourceId);
        client.setClientSecret(passwordEncoder.encode(client.getClientSecret()));
        customOAuth2ClientRepository.save(client);
        return new ClientAuth().setClientId(clientId).setClientSecret(client.getClientSecret());
      }
    }.execute();
  }

}
