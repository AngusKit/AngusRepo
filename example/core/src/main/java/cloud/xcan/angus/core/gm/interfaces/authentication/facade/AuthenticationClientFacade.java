package cloud.xcan.angus.core.gm.interfaces.authentication.facade;

import cloud.xcan.angus.api.gm.client.dto.AuthClientAddDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientReplaceDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientSignInDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientSignupDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientUpdateDto;
import cloud.xcan.angus.api.gm.client.vo.AuthClientSignVo;
import cloud.xcan.angus.api.gm.client.vo.AuthClientSignupVo;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.ClientFindDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.AuthClientDetailVo;
import cloud.xcan.angus.spec.experimental.IdKey;
import java.util.HashSet;
import java.util.List;

/**
 * OAuth2客户端认证门面接口 提供OAuth2客户端的登录和注册功能
 */
public interface AuthenticationClientFacade {

  /**
   * OAuth2客户端登录认证
   */
  AuthClientSignVo signin(AuthClientSignInDto dto);

  /**
   * OAuth2客户端注册
   */
  AuthClientSignupVo signupByDoor(AuthClientSignupDto dto);

  /**
   * 创建OAuth2客户端
   */
  IdKey<String, Object> add(AuthClientAddDto dto);

  /**
   * 更新OAuth2客户端配置
   */
  void update(AuthClientUpdateDto dto);

  /**
   * 替换OAuth2客户端配置
   */
  IdKey<String, Object> replace(AuthClientReplaceDto dto);

  /**
   * 删除OAuth2客户端
   */
  void delete(HashSet<String> clientIds);

  /**
   * 获取OAuth2客户端详情
   */
  AuthClientDetailVo detail(String id);

  /**
   * 获取OAuth2客户端列表
   */
  List<AuthClientDetailVo> list(ClientFindDto dto);

}
