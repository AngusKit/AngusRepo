package cloud.xcan.angus.core.gm.interfaces.user;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserFacade;
import cloud.xcan.angus.api.gm.user.dto.ChangePasswordDto;
import cloud.xcan.angus.api.gm.user.dto.CheckPasswordDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserBatchDeleteDto;
import cloud.xcan.angus.api.gm.user.dto.UserCreateDto;
import cloud.xcan.angus.api.gm.user.dto.UserFindDto;
import cloud.xcan.angus.api.gm.user.dto.UserLockDto;
import cloud.xcan.angus.api.gm.user.dto.UserPatchDto;
import cloud.xcan.angus.api.gm.user.dto.UserUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserCurrentDetailVo;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
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

@Tag(name = "User", description = "用户管理 - 用户的创建、管理、统计等功能")
@Validated
@RestController
@RequestMapping("/api/v1/users")
public class UserRest {

  @Resource
  private UserFacade userFacade;

  @Operation(operationId = "createUser", summary = "创建用户", description = "创建新用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "创建成功"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<UserDetailVo> create(
      @Valid @RequestBody UserCreateDto dto) {
    return ApiLocaleResult.success(userFacade.create(dto));
  }

  @Operation(operationId = "updateUser", summary = "更新用户", description = "更新用户基本信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "404", description = "用户不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}")
  public ApiLocaleResult<UserDetailVo> update(
      @Parameter(description = "用户ID") @PathVariable Long id,
      @Valid @RequestBody UserUpdateDto dto) {
    return ApiLocaleResult.success(userFacade.update(id, dto));
  }

  @Operation(operationId = "patchUser", summary = "部分更新用户", description = "部分更新用户信息，只更新提供的字段")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "404", description = "用户不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}")
  public ApiLocaleResult<UserDetailVo> patch(
      @Parameter(description = "用户ID") @PathVariable Long id,
      @Valid @RequestBody UserPatchDto dto) {
    return ApiLocaleResult.success(userFacade.patch(id, dto));
  }

  @Operation(operationId = "updateUserStatus", summary = "启用/禁用用户", description = "更新用户启用状态")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "状态更新成功"),
      @ApiResponse(responseCode = "404", description = "用户不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}/status")
  public ApiLocaleResult<UserStatusUpdateVo> updateStatus(
      @Parameter(description = "用户ID") @PathVariable Long id,
      @Valid @RequestBody EnabledStatusUpdateDto dto) {
    return ApiLocaleResult.success(userFacade.updateStatus(id, dto));
  }

  @Operation(operationId = "updateUserLock", summary = "锁定/解锁用户", description = "锁定或解锁指定用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "操作成功"),
      @ApiResponse(responseCode = "404", description = "用户不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}/lock")
  public ApiLocaleResult<UserLockVo> updateLock(
      @Parameter(description = "用户ID") @PathVariable Long id,
      @Valid @RequestBody UserLockDto dto) {
    return ApiLocaleResult.success(userFacade.updateLock(id, dto));
  }

  @Operation(operationId = "changePassword", summary = "修改当前用户密码", description = "当前用户修改密码")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "密码修改成功"),
      @ApiResponse(responseCode = "400", description = "原密码错误")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/change-password/current")
  public ApiLocaleResult<?> changeCurrentPassword(@Valid @RequestBody ChangePasswordDto dto) {
    userFacade.changeCurrentPassword(dto);
    return ApiLocaleResult.success();
  }

  @Operation(operationId = "checkUserPassword", summary = "检查用户密码是否正确",
      description = "检查指定用户的密码是否正确")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "密码正确"),
      @ApiResponse(responseCode = "400", description = "密码错误")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/check-password")
  public ApiLocaleResult<?> checkPassword(
      @Parameter(description = "用户ID") @PathVariable Long id,
      @Valid @RequestBody CheckPasswordDto dto) {
    userFacade.checkPassword(id, dto.getPassword());
    return ApiLocaleResult.success();
  }

  @Operation(operationId = "deleteUser", summary = "删除用户", description = "删除指定用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功"),
      @ApiResponse(responseCode = "404", description = "用户不存在")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(description = "用户ID") @PathVariable Long id) {
    userFacade.delete(id);
  }

  @Operation(operationId = "batchDeleteUsers", summary = "批量删除用户", description = "批量删除指定用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/batch")
  public void batchDelete(@Valid @RequestBody UserBatchDeleteDto dto) {
    userFacade.batchDelete(dto);
  }

  @Operation(operationId = "getUserDetail", summary = "获取用户详情",
      description = "获取指定用户的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "用户详情获取成功"),
      @ApiResponse(responseCode = "404", description = "用户不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<UserDetailVo> getDetail(
      @Parameter(description = "用户ID") @PathVariable Long id) {
    return ApiLocaleResult.success(userFacade.getDetail(id));
  }

  @Operation(operationId = "getCurrentUser", summary = "获取当前用户信息",
      description = "获取当前登录用户的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "用户信息获取成功"),
      @ApiResponse(responseCode = "401", description = "未登录")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/current")
  public ApiLocaleResult<UserCurrentDetailVo> getCurrentUser(
      @Parameter(name = "appCode", description = "访问应用编码", required = false)
      @RequestParam(value = "appCode", required = false) String appCode,
      @Parameter(name = "editionType", description = "访问应用版本类型", required = false)
      @RequestParam(value = "editionType", required = false) EditionType editionType) {
    return ApiLocaleResult.success(userFacade.getCurrent(appCode, editionType));
  }

  @Operation(operationId = "getUserList", summary = "获取用户列表",
      description = "获取用户列表，支持分页、搜索和筛选")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "用户列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<UserListVo>> list(
      @Valid @ParameterObject UserFindDto dto) {
    return ApiLocaleResult.success(userFacade.list(dto));
  }

  @Operation(operationId = "getUserStats", summary = "获取用户统计数据",
      description = "获取用户统计数据，包括总数、激活/禁用数量、待接收邀请数、过去7天活跃率等。支持按应用编码筛选。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "统计数据获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<UserStatsVo> getStats(
      @Parameter(description = "应用编码，指定时仅统计该应用下的用户") @RequestParam(value = "appCode", required = false) String appCode) {
    return ApiLocaleResult.success(userFacade.getStats(appCode));
  }

}
