package cloud.xcan.angus.core.gm.application.cmd.authentication;

import cloud.xcan.angus.api.commonlink.client.enums.ClientSource;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;


/**
 * OAuth2客户端命令服务接口 负责OAuth2客户端的写操作，包括创建、更新、删除等
 */
public interface AuthenticationClientCmd {

  /**
   * 创建OAuth2客户端
   */
  IdKey<String, Object> add(CustomOAuth2RegisteredClient client);

  /**
   * 更新OAuth2客户端配置
   */
  void update(CustomOAuth2RegisteredClient client);

  /**
   * 替换OAuth2客户端配置
   */
  IdKey<String, Object> replace(CustomOAuth2RegisteredClient client);

  /**
   * 删除OAuth2客户端
   */
  void delete(HashSet<String> clientIds);

  /**
   * 删除系统令牌客户端
   */
  void deleteSystemTokenClient(String tokenName, ClientSource source);

  /**
   * 更新访问令牌的存活时间
   */
  void updateAccessTokenTimeToLive(int accessTokenTimeInSeconds);
}
