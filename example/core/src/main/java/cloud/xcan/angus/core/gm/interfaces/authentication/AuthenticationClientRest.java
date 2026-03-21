package cloud.xcan.angus.core.gm.interfaces.authentication;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_BATCH_SIZE;

import cloud.xcan.angus.api.gm.client.dto.AuthClientAddDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientReplaceDto;
import cloud.xcan.angus.api.gm.client.dto.AuthClientUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.AuthenticationClientFacade;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.ClientFindDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.AuthClientDetailVo;
import cloud.xcan.angus.core.spring.condition.CloudServiceEditionCondition;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.spec.annotations.OperationClient;
import cloud.xcan.angus.spec.experimental.IdKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AuthenticationClient", description = "管理OAuth2客户端的注册和配置。并安全地交换凭证以获取访问令牌，用于与受保护的API交互")
@OperationClient
@PreAuthorize("@PPS.isOpClient()")
@Conditional(value = CloudServiceEditionCondition.class)
@Validated
@RestController
@RequestMapping("/api/v1/auth/client")
public class AuthenticationClientRest {

  @Resource
  private AuthenticationClientFacade authenticationClientFacade;

  @Operation(operationId = "createOAuthClient", summary = "创建OAuth2客户端")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "OAuth2客户端创建成功")})
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<IdKey<String, Object>> add(@Valid @RequestBody AuthClientAddDto dto) {
    return ApiLocaleResult.success(authenticationClientFacade.add(dto));
  }

  @Operation(operationId = "updateOAuthClient", summary = "更新OAuth2客户端配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "客户端配置更新成功")
  })
  @PatchMapping
  public ApiLocaleResult<?> update(@Valid @RequestBody AuthClientUpdateDto dto) {
    authenticationClientFacade.update(dto);
    return ApiLocaleResult.success();
  }

  @Operation(operationId = "replaceOAuthClient", summary = "替换OAuth2客户端配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "客户端配置替换成功")
  })
  @PutMapping
  public ApiLocaleResult<IdKey<String, Object>> replace(
      @Valid @RequestBody AuthClientReplaceDto dto) {
    return ApiLocaleResult.success(authenticationClientFacade.replace(dto));
  }

  @Operation(operationId = "deleteOAuthClient", summary = "删除OAuth2客户端")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "客户端删除成功")})
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void delete(
      @Valid @NotEmpty @Size(max = MAX_BATCH_SIZE) @RequestParam("ids") HashSet<String> clientIds) {
    authenticationClientFacade.delete(clientIds);
  }

  @Operation(operationId = "getOAuthClientDetails", summary = "获取OAuth2客户端详情")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "客户端详情获取成功")})
  @GetMapping(value = "/{id}")
  public ApiLocaleResult<AuthClientDetailVo> detail(
      @Parameter(name = "id", description = "OAuth2客户端标识符", required = true) @PathVariable("id") String id) {
    return ApiLocaleResult.success(authenticationClientFacade.detail(id));
  }

  @Operation(operationId = "getOAuthClientList", summary = "获取OAuth2客户端列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "客户端列表获取成功")})
  @GetMapping
  public ApiLocaleResult<List<AuthClientDetailVo>> list(@Valid ClientFindDto dto) {
    return ApiLocaleResult.success(authenticationClientFacade.list(dto));
  }

}
