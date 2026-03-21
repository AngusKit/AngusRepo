package cloud.xcan.angus.core.gm.interfaces.authorization;

import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.AuthorizationFacade;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationBatchCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationBatchDeleteDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationFindDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationRoleAddDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationBatchVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationListVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationRoleVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationStatsVo;
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

@Tag(name = "Authorization", description = "授权管理 - 用户/部门/组的角色授权管理")
@Validated
@RestController
@RequestMapping("/api/v1/authorizations")
public class AuthorizationRest {

  @Resource
  private AuthorizationFacade authorizationFacade;

  @Operation(operationId = "createAuthorization", summary = "创建授权", description = "创建新授权记录")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "授权创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<AuthorizationDetailVo> create(
      @Valid @RequestBody AuthorizationCreateDto dto) {
    return ApiLocaleResult.success(authorizationFacade.create(dto));
  }

  @Operation(operationId = "batchCreateAuthorization", summary = "批量授权",
      description = "为多个目标批量创建授权")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "批量授权成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/batch")
  public ApiLocaleResult<AuthorizationBatchVo> batchCreate(
      @Valid @RequestBody AuthorizationBatchCreateDto dto) {
    return ApiLocaleResult.success(authorizationFacade.batchCreate(dto));
  }

  @Operation(operationId = "updateAuthorization", summary = "更新授权", description = "更新授权信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}")
  public ApiLocaleResult<AuthorizationDetailVo> update(
      @Parameter(description = "授权ID") @PathVariable Long id,
      @Valid @RequestBody AuthorizationUpdateDto dto) {
    return ApiLocaleResult.success(authorizationFacade.update(id, dto));
  }

  @Operation(operationId = "addRolesToAuthorization", summary = "添加角色到授权",
      description = "向指定授权添加角色")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "角色添加成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/roles")
  public ApiLocaleResult<AuthorizationRoleVo> addRoles(
      @Parameter(description = "授权ID") @PathVariable Long id,
      @Valid @RequestBody AuthorizationRoleAddDto dto) {
    return ApiLocaleResult.success(authorizationFacade.addRoles(id, dto));
  }

  @Operation(operationId = "removeRoleFromAuthorization", summary = "从授权中移除角色",
      description = "从指定授权中移除角色")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "角色移除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PostMapping("/{id}/roles/{roleId}")
  public ApiLocaleResult<AuthorizationRoleVo> removeRole(
      @Parameter(description = "授权ID") @PathVariable Long id,
      @Parameter(description = "角色ID") @PathVariable Long roleId) {
    return ApiLocaleResult.success(authorizationFacade.removeRole(id, roleId));
  }

  @Operation(operationId = "addRolesToAuthorizationBySubject", summary = "根据授权主体添加角色到授权",
      description = "根据授权主体添加角色到授权")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "角色添加成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/subject/{subjectType}/{subjectId}/roles")
  public ApiLocaleResult<AuthorizationRoleVo> addRolesToAuthorizationBySubject(
      @Parameter(description = "授权主体类型") @PathVariable AuthorizationSubjectType subjectType,
      @Parameter(description = "授权主体ID") @PathVariable Long subjectId,
      @Valid @RequestBody AuthorizationRoleAddDto dto) {
    return ApiLocaleResult.success(
        authorizationFacade.addRolesToAuthorizationBySubject(subjectType, subjectId, dto));
  }

  @Operation(operationId = "removeRoleFromAuthorizationBySubject", summary = "根据授权主体从授权中移除角色",
      description = "根据授权主体从授权中移除角色")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "角色移除成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/subject/{subjectType}/{subjectId}/roles/{roleId}")
  public ApiLocaleResult<AuthorizationRoleVo> removeRoleFromAuthorizationBySubject(
      @Parameter(description = "授权主体类型") @PathVariable AuthorizationSubjectType subjectType,
      @Parameter(description = "授权主体ID") @PathVariable Long subjectId,
      @Parameter(description = "角色ID") @PathVariable Long roleId) {
    return ApiLocaleResult.success(
        authorizationFacade.removeRoleFromAuthorizationBySubject(subjectType, subjectId, roleId));
  }

  @Operation(operationId = "deleteAuthorization", summary = "删除授权", description = "删除指定授权")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(description = "授权ID") @PathVariable Long id) {
    authorizationFacade.delete(id);
  }

  @Operation(operationId = "batchDeleteAuthorization", summary = "批量删除授权",
      description = "批量删除授权记录")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "批量删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/batch")
  public void batchDelete(
      @Valid @RequestBody AuthorizationBatchDeleteDto dto) {
    authorizationFacade.batchDelete(dto);
  }

  @Operation(operationId = "getAuthorizationDetail", summary = "获取授权详情",
      description = "获取指定授权的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "授权详情获取成功"),
      @ApiResponse(responseCode = "404", description = "授权不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<AuthorizationDetailVo> getDetail(
      @Parameter(description = "授权ID") @PathVariable Long id) {
    return ApiLocaleResult.success(authorizationFacade.getDetail(id));
  }

  @Operation(operationId = "getSubjectAuthorization", summary = "获取主体授权信息",
      description = "获取指定主体的授权信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "主体授权信息获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/subject/{subjectType}/{subjectId}")
  public ApiLocaleResult<AuthorizationDetailVo> getSubjectAuthorization(
      @Parameter(description = "授权主体类型") @PathVariable AuthorizationSubjectType subjectType,
      @Parameter(description = "授权主体ID") @PathVariable Long subjectId) {
    return ApiLocaleResult.success(
        authorizationFacade.getSubjectAuthorization(subjectType, subjectId));
  }

  @Operation(operationId = "getAuthorizationList", summary = "获取授权列表",
      description = "获取授权记录列表，支持分页和筛选")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "授权列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<AuthorizationListVo>> list(
      @Valid @ParameterObject AuthorizationFindDto dto) {
    return ApiLocaleResult.success(authorizationFacade.list(dto));
  }

  @Operation(operationId = "getUserAuthorizationList", summary = "获取用户授权列表",
      description = "获取用户类型的授权记录")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "用户授权列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/users")
  public ApiLocaleResult<PageResult<AuthorizationListVo>> listUsers(
      @Valid @ParameterObject AuthorizationFindDto dto) {
    return ApiLocaleResult.success(authorizationFacade.listUsers(dto));
  }

  @Operation(operationId = "getDepartmentAuthorizationList", summary = "获取部门授权列表",
      description = "获取部门类型的授权记录")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "部门授权列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/departments")
  public ApiLocaleResult<PageResult<AuthorizationListVo>> listDepartments(
      @Valid @ParameterObject AuthorizationFindDto dto) {
    return ApiLocaleResult.success(authorizationFacade.listDepartments(dto));
  }

  @Operation(operationId = "getGroupAuthorizationList", summary = "获取组授权列表",
      description = "获取组类型的授权记录")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "组授权列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/groups")
  public ApiLocaleResult<PageResult<AuthorizationListVo>> listGroups(
      @Valid @ParameterObject AuthorizationFindDto dto) {
    return ApiLocaleResult.success(authorizationFacade.listGroups(dto));
  }

  @Operation(operationId = "getAuthorizationStats", summary = "获取授权统计数据",
      description = "获取授权统计数据，包括总授权数、用户授权数、部门授权数、组授权数")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "授权统计数据获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<AuthorizationStatsVo> getStats() {
    return ApiLocaleResult.success(authorizationFacade.getStats());
  }
}
