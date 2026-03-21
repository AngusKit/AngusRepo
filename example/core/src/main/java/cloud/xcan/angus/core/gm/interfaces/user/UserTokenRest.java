package cloud.xcan.angus.core.gm.interfaces.user;

import cloud.xcan.angus.api.gm.user.dto.TokenCreateDto;
import cloud.xcan.angus.api.gm.user.dto.TokenUpdateDto;
import cloud.xcan.angus.api.gm.user.dto.TokensQueryDto;
import cloud.xcan.angus.api.gm.user.vo.UserTokenVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserTokenFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.TokenQuotaVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserToken", description = "用户令牌管理 - 个人访问令牌的增删改查和权限管理")
@Validated
@RestController
@RequestMapping("/api/v1/user/tokens")
public class UserTokenRest {

  @Resource
  private UserTokenFacade userTokenFacade;

  @Operation(operationId = "createToken", summary = "创建用户令牌", description = "创建新的个人访问令牌")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "令牌创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<UserTokenVo> create(@Valid @RequestBody TokenCreateDto dto) {
    return ApiLocaleResult.success(userTokenFacade.create(dto));
  }

  @Operation(operationId = "updateToken", summary = "更新令牌信息",
      description = "更新令牌的名称和描述（不能修改权限范围和过期时间）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{tokenId}")
  public ApiLocaleResult<UserTokenVo> update(
      @Parameter(description = "令牌ID") @PathVariable Long tokenId,
      @Valid @RequestBody TokenUpdateDto dto) {
    return ApiLocaleResult.success(userTokenFacade.update(tokenId, dto));
  }

  @Operation(operationId = "revokeToken", summary = "撤销令牌", description = "撤销指定的令牌（将状态改为REVOKED）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "令牌已撤销")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{tokenId}/revoke")
  public ApiLocaleResult<UserTokenVo> revoke(
      @Parameter(description = "令牌ID") @PathVariable Long tokenId) {
    return ApiLocaleResult.success(userTokenFacade.revoke(tokenId));
  }

  @Operation(operationId = "deleteToken", summary = "删除令牌", description = "永久删除指定的令牌")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{tokenId}")
  public void delete(@Parameter(description = "令牌ID") @PathVariable Long tokenId) {
    userTokenFacade.delete(tokenId);
  }

  @Operation(operationId = "getTokenDetail", summary = "获取令牌详情", description = "获取指定令牌的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{tokenId}")
  public ApiLocaleResult<UserTokenVo> getDetail(
      @Parameter(description = "令牌ID") @PathVariable Long tokenId) {
    return ApiLocaleResult.success(userTokenFacade.getDetail(tokenId));
  }

  @Operation(operationId = "listTokens", summary = "获取令牌列表", description = "获取当前用户的所有令牌列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<UserTokenVo>> list(
      @Valid @ParameterObject TokensQueryDto dto) {
    return ApiLocaleResult.success(userTokenFacade.list(dto));
  }

  @Operation(operationId = "getTokenQuota", summary = "获取令牌配额统计",
      description = "获取当前用户的令牌配额信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/quota")
  public ApiLocaleResult<TokenQuotaVo> getQuota() {
    return ApiLocaleResult.success(userTokenFacade.getQuota());
  }
}
