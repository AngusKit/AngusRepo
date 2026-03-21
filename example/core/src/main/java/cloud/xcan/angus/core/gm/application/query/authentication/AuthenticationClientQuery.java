package cloud.xcan.angus.core.gm.application.query.authentication;

import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import java.util.List;


/**
 * OAuth2客户端查询服务接口 负责OAuth2客户端的读操作，包括查询、验证等
 */
public interface AuthenticationClientQuery {

  /**
   * 根据ID获取OAuth2客户端详情
   */
  CustomOAuth2RegisteredClient detail(String id);

  /**
   * 查询OAuth2客户端列表，支持按ID、客户端ID、租户ID过滤
   */
  List<CustomOAuth2RegisteredClient> list(String id, String clientId, String tenantId);

  /**
   * 验证并查找OAuth2客户端
   */
  CustomOAuth2RegisteredClient checkAndFind(String clientId);

  /**
   * 验证客户端密钥并查找OAuth2客户端
   */
  CustomOAuth2RegisteredClient checkAndFind(String clientId, String clientSecret);

  /**
   * 验证客户端密钥和权限范围并查找OAuth2客户端
   */
  CustomOAuth2RegisteredClient checkAndFind(String clientId, String clientSecret, String scope);

  /**
   * 验证并查找OAuth2客户端，可选择是否检查启用状态
   */
  CustomOAuth2RegisteredClient checkAndFind(String clientId, boolean checkEnabled);

  /**
   * 查找有效的OAuth2客户端（不抛出异常）
   */
  CustomOAuth2RegisteredClient findValidByClientId0(String clientId);

}
