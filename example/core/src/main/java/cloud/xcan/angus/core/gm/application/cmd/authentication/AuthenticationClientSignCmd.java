package cloud.xcan.angus.core.gm.application.cmd.authentication;

import cloud.xcan.angus.api.commonlink.client.ClientAuth;
import cloud.xcan.angus.api.commonlink.client.enums.Client2pSignupBiz;
import java.util.Map;

/**
 * OAuth2客户端认证命令服务接口 负责OAuth2客户端的登录和注册操作
 */
public interface AuthenticationClientSignCmd {

  /**
   * OAuth2客户端登录认证
   */
  Map<String, String> signin(String clientId, String clientSecret, String scope);

  /**
   * OAuth2客户端注册
   */
  ClientAuth signupByDoor(Client2pSignupBiz signupBiz, Long tenantId, String tenantName,
      Long resourceId);
}
