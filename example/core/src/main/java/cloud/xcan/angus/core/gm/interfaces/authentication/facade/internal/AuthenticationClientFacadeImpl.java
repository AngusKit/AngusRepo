package cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal;


import static cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal.assembler.AuthenticationClientAssembler.addDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal.assembler.AuthenticationClientAssembler.replaceDtoToDomain;
import static cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal.assembler.AuthenticationClientAssembler.signInToVo;
import static cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal.assembler.AuthenticationClientAssembler.signup2Vo;
import static cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal.assembler.AuthenticationClientAssembler.toDetailVo;
import static cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal.assembler.AuthenticationClientAssembler.updateDtoToDomain;

import cloud.xcan.angus.api.commonlink.client.ClientAuth;
import cloud.xcan.angus.api.gm.client.dto.AuthClientAddDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientReplaceDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientSignInDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientSignupDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientUpdateDto;
import cloud.xcan.angus.api.gm.client.vo.AuthClientSignVo;
import cloud.xcan.angus.api.gm.client.vo.AuthClientSignupVo;
import cloud.xcan.angus.core.gm.application.cmd.authentication.AuthenticationClientCmd;
import cloud.xcan.angus.core.gm.application.cmd.authentication.AuthenticationClientSignCmd;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthenticationClientQuery;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.AuthenticationClientFacade;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.ClientFindDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal.assembler.AuthenticationClientAssembler;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.AuthClientDetailVo;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.spec.experimental.IdKey;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * OAuth2客户端认证门面实现 协调应用层服务，处理DTO/VO转换
 */
@Component
public class AuthenticationClientFacadeImpl implements AuthenticationClientFacade {

  @Resource
  private AuthenticationClientSignCmd authClientSignCmd;

  @Resource
  private AuthenticationClientQuery authClientQuery;

  @Resource
  private AuthenticationClientCmd authClientCmd;

  @Override
  public AuthClientSignVo signin(AuthClientSignInDto dto) {
    Map<String, String> result = authClientSignCmd.signin(dto.getClientId(),
        dto.getClientSecret(), dto.getScope());
    return signInToVo(result);
  }

  @Override
  public AuthClientSignupVo signupByDoor(AuthClientSignupDto dto) {
    ClientAuth clientAuth = authClientSignCmd.signupByDoor(dto.getSignupBiz(), dto.getTenantId(),
        dto.getTenantName(), dto.getResourceId());
    return signup2Vo(clientAuth);
  }

  @Override
  public IdKey<String, Object> add(AuthClientAddDto dto) {
    return authClientCmd.add(addDtoToDomain(dto));
  }

  @Override
  public void update(AuthClientUpdateDto dto) {
    authClientCmd.update(updateDtoToDomain(dto));
  }

  @Override
  public IdKey<String, Object> replace(AuthClientReplaceDto dto) {
    return authClientCmd.replace(replaceDtoToDomain(dto));
  }

  @Override
  public void delete(HashSet<String> clientIds) {
    authClientCmd.delete(clientIds);
  }

  @Override
  public AuthClientDetailVo detail(String id) {
    return toDetailVo(authClientQuery.detail(id));
  }

  @Override
  public List<AuthClientDetailVo> list(ClientFindDto dto) {
    List<CustomOAuth2RegisteredClient> clients = authClientQuery.list(dto.getId(),
        dto.getClientId(), dto.getTenantId());
    return clients.stream().map(AuthenticationClientAssembler::toDetailVo)
        .collect(Collectors.toList());
  }

}
