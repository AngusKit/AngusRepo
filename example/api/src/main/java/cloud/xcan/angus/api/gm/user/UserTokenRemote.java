package cloud.xcan.angus.api.gm.user;

import cloud.xcan.angus.api.gm.user.dto.TokenCreateDto;
import cloud.xcan.angus.api.gm.user.dto.TokenUpdateDto;
import cloud.xcan.angus.api.gm.user.dto.TokensQueryDto;
import cloud.xcan.angus.api.gm.user.vo.UserTokenVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient(name = "${xcan.service.gm:XCAN-ANGUSGM.BOOT}")
public interface UserTokenRemote {

  @Operation(operationId = "createToken", summary = "创建用户令牌", description = "创建新的个人访问令牌")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "令牌创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(value = "/api/v1/user/tokens")
  ApiLocaleResult<UserTokenVo> create(@Valid @RequestBody TokenCreateDto dto);

  @Operation(operationId = "updateToken", summary = "更新令牌信息",
      description = "更新令牌的名称和描述（不能修改权限范围和过期时间）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/api/v1/user/tokens/{tokenId}")
  ApiLocaleResult<UserTokenVo> update(
      @Parameter(description = "令牌ID") @PathVariable Long tokenId,
      @Valid @RequestBody TokenUpdateDto dto);

  @Operation(operationId = "revokeToken", summary = "撤销令牌", description = "撤销指定的令牌（将状态改为REVOKED）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "令牌已撤销")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/api/v1/user/tokens/{tokenId}/revoke")
  ApiLocaleResult<UserTokenVo> revoke(
      @Parameter(description = "令牌ID") @PathVariable Long tokenId);

  @Operation(operationId = "deleteToken", summary = "删除令牌", description = "永久删除指定的令牌")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/api/v1/user/tokens/{tokenId}")
  void delete(@Parameter(description = "令牌ID") @PathVariable Long tokenId);

  @Operation(operationId = "getTokenDetail", summary = "获取令牌详情", description = "获取指定令牌的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/api/v1/user/tokens/{tokenId}")
  ApiLocaleResult<UserTokenVo> getDetail(
      @Parameter(description = "令牌ID") @PathVariable Long tokenId);

  @Operation(operationId = "listTokens", summary = "获取令牌列表", description = "获取当前用户的所有令牌列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = "/api/v1/user/tokens")
  ApiLocaleResult<PageResult<UserTokenVo>> list(
      @Valid @ParameterObject @SpringQueryMap TokensQueryDto dto);

}
