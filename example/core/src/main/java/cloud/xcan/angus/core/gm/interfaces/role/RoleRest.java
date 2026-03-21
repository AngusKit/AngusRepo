package cloud.xcan.angus.core.gm.interfaces.role;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.api.gm.RolePermissionVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.role.facade.RoleFacade;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RoleCreateDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RoleDefaultDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RoleFindDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RoleObjectFindDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RolePermissionUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RoleUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.vo.AuthorizableApplicationMenuVo;
import cloud.xcan.angus.core.gm.interfaces.role.facade.vo.RoleDefaultVo;
import cloud.xcan.angus.core.gm.interfaces.role.facade.vo.RoleDetailVo;
import cloud.xcan.angus.core.gm.interfaces.role.facade.vo.RoleListVo;
import cloud.xcan.angus.core.gm.interfaces.role.facade.vo.RoleStatsVo;
import cloud.xcan.angus.api.gm.user.vo.UserListVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
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

@Tag(name = "Role", description = "角色管理 - 角色的创建、管理、权限配置等功能")
@Validated
@RestController
@RequestMapping("/api/v1/roles")
public class RoleRest {

  @Resource
  private RoleFacade roleFacade;

  @Operation(operationId = "createRole", summary = "创建角色", description = "创建新角色")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "角色创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<RoleDetailVo> create(
      @Valid @RequestBody RoleCreateDto dto) {
    return ApiLocaleResult.success(roleFacade.create(dto));
  }

  @Operation(operationId = "updateRole", summary = "更新角色", description = "更新角色基本信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "404", description = "角色不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}")
  public ApiLocaleResult<RoleDetailVo> update(
      @Parameter(description = "角色ID") @PathVariable Long id,
      @Valid @RequestBody RoleUpdateDto dto) {
    return ApiLocaleResult.success(roleFacade.update(id, dto));
  }

  @Operation(operationId = "updateEnabledStatus", summary = "启用/禁用角色", description = "修改角色状态")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "状态更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}/status")
  public ApiLocaleResult<RoleDetailVo> updateStatus(
      @Parameter(description = "应用ID") @PathVariable Long id,
      @Valid @RequestBody EnabledStatusUpdateDto dto) {
    return ApiLocaleResult.success(roleFacade.updateStatus(id, dto));
  }

  @Operation(operationId = "deleteRole", summary = "删除角色", description = "删除指定角色")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功"),
      @ApiResponse(responseCode = "404", description = "角色不存在")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(description = "角色ID") @PathVariable Long id) {
    roleFacade.delete(id);
  }

  @Operation(operationId = "getRoleDetail", summary = "获取角色详情",
      description = "获取指定角色的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "角色详情获取成功"),
      @ApiResponse(responseCode = "404", description = "角色不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<RoleDetailVo> getDetail(
      @Parameter(description = "角色ID") @PathVariable Long id) {
    return ApiLocaleResult.success(roleFacade.getDetail(id));
  }

  @Operation(operationId = "getRoleList", summary = "获取角色列表",
      description = "获取角色列表，支持分页、搜索和筛选")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "角色列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<RoleListVo>> find(
      @Valid @ParameterObject RoleFindDto dto) {
    return ApiLocaleResult.success(roleFacade.list(dto));
  }

  @Operation(operationId = "getRoleStats", summary = "获取角色统计数据",
      description = "获取角色统计数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "统计数据获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<RoleStatsVo> getStats() {
    return ApiLocaleResult.success(roleFacade.getStats());
  }

  @Operation(operationId = "getRolePermissions", summary = "获取角色权限配置",
      description = "获取指定角色的权限配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "权限配置获取成功"),
      @ApiResponse(responseCode = "404", description = "角色不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/permissions")
  public ApiLocaleResult<RolePermissionVo> getPermissions(
      @Parameter(description = "角色ID") @PathVariable Long id) {
    return ApiLocaleResult.success(roleFacade.getPermissions(id));
  }

  @Operation(operationId = "updateRolePermissions", summary = "更新角色权限",
      description = "更新指定角色的权限配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "权限更新成功"),
      @ApiResponse(responseCode = "404", description = "角色不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}/permissions")
  public ApiLocaleResult<RolePermissionVo> updatePermissions(
      @Parameter(description = "角色ID") @PathVariable Long id,
      @Valid @RequestBody RolePermissionUpdateDto dto) {
    return ApiLocaleResult.success(roleFacade.updatePermissions(id, dto));
  }

  @Operation(operationId = "setDefaultRole", summary = "设置默认角色",
      description = "设置指定角色为默认角色")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "设置成功"),
      @ApiResponse(responseCode = "404", description = "角色不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}/default")
  public ApiLocaleResult<RoleDefaultVo> setDefault(
      @Parameter(description = "角色ID") @PathVariable Long id,
      @Valid @RequestBody RoleDefaultDto dto) {
    return ApiLocaleResult.success(roleFacade.setDefault(id, dto));
  }

  @Operation(operationId = "getRoleWideUsers", summary = "获取角色所有用户列表",
      description = "获取指定角色下的用户列表，包含授权主体是部门和组的关联用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "用户列表获取成功"),
      @ApiResponse(responseCode = "404", description = "角色不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/wide-users")
  public ApiLocaleResult<PageResult<UserListVo>> getWideUsers(
      @Parameter(description = "角色ID") @PathVariable Long id,
      @Valid @ParameterObject RoleObjectFindDto dto) {
    return ApiLocaleResult.success(roleFacade.getWideUsers(id, dto));
  }

  @Operation(operationId = "getRoleUsers", summary = "获取角色用户列表",
      description = "获取指定角色下的用户列表，不包含授权主体是部门和组的关联用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "用户列表获取成功"),
      @ApiResponse(responseCode = "404", description = "角色不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/users")
  public ApiLocaleResult<PageResult<UserListVo>> getUsers(
      @Parameter(description = "角色ID") @PathVariable Long id,
      @Valid @ParameterObject RoleObjectFindDto dto) {
    return ApiLocaleResult.success(roleFacade.getUsers(id, dto));
  }

  @Operation(operationId = "getRoleDepartments", summary = "获取角色部门列表",
      description = "获取指定角色下的部门列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "部门列表获取成功"),
      @ApiResponse(responseCode = "404", description = "角色不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/departments")
  public ApiLocaleResult<PageResult<DepartmentDetailVo>> getDepartments(
      @Parameter(description = "角色ID") @PathVariable Long id,
      @Valid @ParameterObject RoleObjectFindDto dto) {
    return ApiLocaleResult.success(roleFacade.getDepartments(id, dto));
  }

  @Operation(operationId = "getRoleGroups", summary = "获取角色组列表",
      description = "获取指定角色下的组列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "组列表获取成功"),
      @ApiResponse(responseCode = "404", description = "角色不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/groups")
  public ApiLocaleResult<PageResult<GroupDetailVo>> getGroups(
      @Parameter(description = "角色ID") @PathVariable Long id,
      @Valid @ParameterObject RoleObjectFindDto dto) {
    return ApiLocaleResult.success(roleFacade.getGroups(id, dto));
  }

  @Operation(operationId = "getAuthorizableMenus", summary = "获取可授权应用菜单树",
      description = "根据角色ID查询可授权应用菜单树。已授权菜单标记为 authorized=true。"
          + "系统管理员可以看到所有应用菜单，非系统管理员只能看到当前用户已授权的应用菜单。")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "可授权应用菜单树获取成功"),
      @ApiResponse(responseCode = "404", description = "角色不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/authorizable-menus")
  public ApiLocaleResult<List<AuthorizableApplicationMenuVo>> getAuthorizableMenus(
      @Parameter(description = "角色ID") @PathVariable Long id) {
    return ApiLocaleResult.success(roleFacade.getAuthorizableMenus(id));
  }

}
