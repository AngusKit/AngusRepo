package cloud.xcan.angus.api.gm.user;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.api.gm.user.dto.ChangePasswordDto;
import cloud.xcan.angus.api.gm.user.dto.CheckPasswordDto;
import cloud.xcan.angus.api.gm.user.dto.UserCreateDto;
import cloud.xcan.angus.api.gm.user.dto.UserFindDto;
import cloud.xcan.angus.api.gm.user.dto.UserLockDto;
import cloud.xcan.angus.api.gm.user.dto.UserPatchDto;
import cloud.xcan.angus.api.gm.user.dto.UserUpdateDto;
import cloud.xcan.angus.api.gm.user.vo.UserDetailVo;
import cloud.xcan.angus.api.gm.user.vo.UserListVo;
import cloud.xcan.angus.api.gm.user.vo.UserLockVo;
import cloud.xcan.angus.api.gm.user.vo.UserStatsVo;
import cloud.xcan.angus.api.gm.user.vo.UserStatusUpdateVo;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient(name = "${xcan.service.gm:XCAN-ANGUSGM.BOOT}")
public interface UserRemote {

  @Operation(operationId = "createUser", summary = "创建用户", description = "创建新用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "创建成功"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(value = "/api/v1/users")
  ApiLocaleResult<UserDetailVo> create(@Valid @RequestBody UserCreateDto dto);

  @Operation(operationId = "updateUser", summary = "更新用户", description = "更新用户基本信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "404", description = "用户不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/api/v1/users/{id}")
  ApiLocaleResult<UserDetailVo> update(@Parameter(description = "用户ID") @PathVariable Long id,
      @Valid @RequestBody UserUpdateDto dto);

  @Operation(operationId = "patchUser", summary = "部分更新用户", description = "部分更新用户信息，只更新提供的字段")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "404", description = "用户不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/api/v1/users/{id}")
  ApiLocaleResult<UserDetailVo> patch(
      @Parameter(description = "用户ID") @PathVariable Long id,
      @Valid @RequestBody UserPatchDto dto);

  @Operation(operationId = "updateUserStatus", summary = "启用/禁用用户", description = "更新用户启用状态")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "状态更新成功"),
      @ApiResponse(responseCode = "404", description = "用户不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/api/v1/users/{id}/status")
  ApiLocaleResult<UserStatusUpdateVo> updateStatus(
      @Parameter(description = "用户ID") @PathVariable Long id,
      @Valid @RequestBody EnabledStatusUpdateDto dto);

  @Operation(operationId = "updateUserLock", summary = "锁定/解锁用户", description = "锁定或解锁指定用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "操作成功"),
      @ApiResponse(responseCode = "404", description = "用户不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/api/v1/users/{id}/lock")
  ApiLocaleResult<UserLockVo> updateLock(
      @Parameter(description = "用户ID") @PathVariable Long id,
      @Valid @RequestBody UserLockDto dto);

  @Operation(operationId = "changePassword", summary = "修改当前用户密码", description = "当前用户修改密码")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "密码修改成功"),
      @ApiResponse(responseCode = "400", description = "原密码错误")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/api/v1/users/change-password/current")
  ApiLocaleResult<?> changeCurrentPassword(@Valid @RequestBody ChangePasswordDto dto);

  @Operation(operationId = "checkUserPassword", summary = "检查用户密码是否正确",
      description = "检查指定用户的密码是否正确")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "密码正确"),
      @ApiResponse(responseCode = "400", description = "密码错误")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/api/v1/users/{id}/check-password")
  ApiLocaleResult<?> checkPassword(
      @Parameter(description = "用户ID") @PathVariable Long id,
      @Valid @RequestBody CheckPasswordDto dto);

  @Operation(operationId = "deleteUser", summary = "删除用户", description = "删除指定用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功"),
      @ApiResponse(responseCode = "404", description = "用户不存在")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/api/v1/users/{id}")
  void delete(@Parameter(description = "用户ID") @PathVariable Long id);

  @Operation(operationId = "getUserDetail", summary = "获取用户详情",
      description = "获取指定用户的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "用户详情获取成功"),
      @ApiResponse(responseCode = "404", description = "用户不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/api/v1/users/{id}")
  ApiLocaleResult<UserDetailVo> getDetail(@Parameter(description = "用户ID") @PathVariable Long id);

  @Operation(operationId = "getUserList", summary = "获取用户列表",
      description = "获取用户列表，支持分页、搜索和筛选")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "用户列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/api/v1/users")
  ApiLocaleResult<PageResult<UserListVo>> list(
      @Valid @ParameterObject @SpringQueryMap UserFindDto dto);

  @Operation(operationId = "getUserStats", summary = "获取用户统计数据",
      description = "获取用户统计数据，包括总数、激活/禁用数量、待接收邀请数、过去7天活跃率等。支持按应用编码筛选。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "统计数据获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/api/v1/users/stats")
  ApiLocaleResult<UserStatsVo> getStats(
      @Parameter(description = "应用编码，指定时仅统计该应用下的用户")
      @RequestParam(value = "appCode", required = false) String appCode);
}
