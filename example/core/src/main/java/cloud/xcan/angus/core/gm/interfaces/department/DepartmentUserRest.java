package cloud.xcan.angus.core.gm.interfaces.department;

import cloud.xcan.angus.core.gm.interfaces.department.facade.DepartmentUserFacade;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUserAddDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUserRemoveDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUserTransferDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentUserAddVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentUserTransferVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentUserVo;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "DepartmentUser", description = "部门用户管理 - 部门用户的添加、移除、转移等功能")
@Validated
@RestController
@RequestMapping("/api/v1/departments/{departmentId}/users")
public class DepartmentUserRest {

  @Resource
  private DepartmentUserFacade departmentUserFacade;

  @Operation(operationId = "addDepartmentUsers", summary = "添加部门用户", description = "向指定部门添加用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "用户添加成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping
  public ApiLocaleResult<DepartmentUserAddVo> addUsers(
      @Parameter(description = "部门ID") @PathVariable Long departmentId,
      @Valid @RequestBody DepartmentUserAddDto dto) {
    return ApiLocaleResult.success(departmentUserFacade.addUsers(departmentId, dto));
  }

  @Operation(operationId = "transferDepartmentUsers", summary = "转移部门用户", description = "将用户从当前部门转移到目标部门")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "用户转移成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/transfer")
  public ApiLocaleResult<DepartmentUserTransferVo> transferUsers(
      @Parameter(description = "源部门ID") @PathVariable Long departmentId,
      @Valid @RequestBody DepartmentUserTransferDto dto) {
    return ApiLocaleResult.success(departmentUserFacade.transferUsers(departmentId, dto));
  }

  @Operation(operationId = "removeDepartmentUser", summary = "移除部门用户", description = "从指定部门移除用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "用户移除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{userId}")
  public void removeUser(
      @Parameter(description = "部门ID") @PathVariable Long departmentId,
      @Parameter(description = "用户ID") @PathVariable Long userId) {
    departmentUserFacade.removeUser(departmentId, userId);
  }

  @Operation(operationId = "batchRemoveDepartmentUsers", summary = "批量移除部门用户", description = "从指定部门批量移除用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "用户批量移除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void removeUsers(
      @Parameter(description = "部门ID") @PathVariable Long departmentId,
      @Valid @RequestBody DepartmentUserRemoveDto dto) {
    departmentUserFacade.removeUsers(departmentId, dto);
  }

  @Operation(operationId = "getDepartmentUsers", summary = "获取部门用户列表", description = "获取指定部门的用户列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "用户列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<DepartmentUserVo>> listUsers(
      @Parameter(description = "部门ID") @PathVariable Long departmentId,
      @Valid @ParameterObject DepartmentUserFindDto dto) {
    return ApiLocaleResult.success(departmentUserFacade.listUsers(departmentId, dto));
  }

  @Operation(operationId = "getUsersNotInDepartment", summary = "获取未加入部门的用户列表", description = "获取未加入指定部门的用户列表，支持分页")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "用户列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/not-in-department")
  public ApiLocaleResult<PageResult<DepartmentUserVo>> listUsersNotInDepartment(
      @Parameter(description = "部门ID") @PathVariable Long departmentId,
      @Valid @ParameterObject DepartmentUserFindDto dto) {
    return ApiLocaleResult.success(
        departmentUserFacade.listUsersNotInDepartment(departmentId, dto));
  }

}
