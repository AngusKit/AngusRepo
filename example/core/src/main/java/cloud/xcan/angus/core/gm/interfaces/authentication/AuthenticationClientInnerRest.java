package cloud.xcan.angus.core.gm.interfaces.authentication;

import cloud.xcan.angus.api.gm.client.dto.AuthClientSignupDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientUpdateDto;
import cloud.xcan.angus.api.gm.client.vo.AuthClientSignupVo;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.AuthenticationClientFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AuthenticationClientInternal", description = "OAuth2客户端注册操作和管理操作的内部API")
@PreAuthorize("hasAuthority('SCOPE_inner_api_trust')")
@Validated
@RestController
@RequestMapping("/innerapi/v1/auth/client")
public class AuthenticationClientInnerRest {

  @Resource
  private AuthenticationClientFacade authenticationClientFacade;

  @Operation(operationId = "signupOAuth2Client", summary = "注册OAuth2客户端",
      description = "为私有应用版本或代理注册新的OAuth2客户端")
  @ResponseStatus(HttpStatus.OK)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OAuth2客户端注册成功")})
  @PostMapping(value = "/signup")
  public ApiLocaleResult<AuthClientSignupVo> signupByDoor(
      @Valid @RequestBody AuthClientSignupDto dto) {
    return ApiLocaleResult.success(authenticationClientFacade.signupByDoor(dto));
  }

  @Operation(operationId = "updateOAuth2Client", summary = "更新OAuth2客户端配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "客户端配置更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping
  public ApiLocaleResult<?> update(@Valid @RequestBody AuthClientUpdateDto dto) {
    authenticationClientFacade.update(dto);
    return ApiLocaleResult.success();
  }

}
