package cloud.xcan.angus.core.gm.interfaces.authentication;

import cloud.xcan.angus.api.gm.client.dto.AuthClientSignInDto;
import cloud.xcan.angus.api.gm.client.vo.AuthClientSignVo;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.AuthenticationClientFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AuthenticationClientPublic", description = "OAuth2客户端认证的公共API，用于获取系统间访问令牌")
@Validated
@RestController
@RequestMapping("/pubapi/v1/auth/client")
public class AuthenticationClientPubRest {

  @Resource
  private AuthenticationClientFacade authenticationClientFacade;

  @Operation(operationId = "signinOAuth2Client", summary = "OAuth2客户端认证",
      description = "为私有应用、第三方授权或内部应用进行客户端认证")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "客户端认证成功")})
  @PostMapping(value = "/signin")
  public ApiLocaleResult<AuthClientSignVo> signin(@Valid @RequestBody AuthClientSignInDto dto) {
    return ApiLocaleResult.success(authenticationClientFacade.signin(dto));
  }

}
