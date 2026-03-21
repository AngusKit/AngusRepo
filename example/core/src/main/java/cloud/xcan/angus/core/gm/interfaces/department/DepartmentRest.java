package cloud.xcan.angus.core.gm.interfaces.department;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.DepartmentFacade;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentCreateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentFindDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentManagerUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentManagerUpdateVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentPathVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentStatsVo;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Department", description = "部门管理 - 部门的创建、管理等功能")
@Validated
@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentRest {

  @Resource
  private DepartmentFacade departmentFacade;

  @Operation(operationId = "createDepartment", summary = "创建部门", description = "创建新部门")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "部门创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<DepartmentDetailVo> create(
      @Valid @RequestBody DepartmentCreateDto dto) {
    return ApiLocaleResult.success(departmentFacade.create(dto));
  }

  @Operation(operationId = "updateDepartment", summary = "更新部门", description = "更新部门基本信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}")
  public ApiLocaleResult<DepartmentDetailVo> update(
      @Parameter(description = "部门ID") @PathVariable Long id,
      @Valid @RequestBody DepartmentUpdateDto dto) {
    return ApiLocaleResult.success(departmentFacade.update(id, dto));
  }

  @Operation(operationId = "updateDepartmentStatus", summary = "禁用/激活部门", description = "修改部门状态")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "状态更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}/status")
  public ApiLocaleResult<DepartmentDetailVo> updateStatus(
      @Parameter(description = "部门ID") @PathVariable Long id,
      @Valid @RequestBody EnabledStatusUpdateDto dto) {
    return ApiLocaleResult.success(departmentFacade.updateStatus(id, dto));
  }

  @Operation(operationId = "updateDepartmentManager", summary = "设置部门负责人", description = "设置指定部门的负责人")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "负责人设置成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}/manager")
  public ApiLocaleResult<DepartmentManagerUpdateVo> updateManager(
      @Parameter(description = "部门ID") @PathVariable Long id,
      @Valid @RequestBody DepartmentManagerUpdateDto dto) {
    return ApiLocaleResult.success(departmentFacade.updateManager(id, dto));
  }

  @Operation(operationId = "deleteDepartment", summary = "删除部门", description = "删除指定部门")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(description = "部门ID") @PathVariable Long id) {
    departmentFacade.delete(id);
  }

  @Operation(operationId = "getDepartmentDetail", summary = "获取部门详情", description = "获取指定部门的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "部门详情获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<DepartmentDetailVo> getDetail(
      @Parameter(description = "部门ID") @PathVariable Long id) {
    return ApiLocaleResult.success(departmentFacade.getDetail(id));
  }

  @Operation(operationId = "getDepartmentList", summary = "获取部门列表", description = "获取部门列表，支持分页、搜索和筛选")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "部门列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<DepartmentDetailVo>> list(
      @Valid @ParameterObject DepartmentFindDto dto) {
    return ApiLocaleResult.success(departmentFacade.list(dto));
  }

  @Operation(operationId = "getDepartmentStats", summary = "获取部门统计数据", description = "获取部门统计数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "统计数据获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<DepartmentStatsVo> getStats() {
    return ApiLocaleResult.success(departmentFacade.getStats());
  }

  @Operation(operationId = "getDepartmentTree", summary = "获取部门树", description = "获取部门树形结构")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "部门树获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/tree")
  public ApiLocaleResult<List<DepartmentDetailVo>> getTree(
      @Parameter(description = "状态筛选") @RequestParam(required = false) EnabledStatus status,
      @Parameter(description = "是否包含用户信息") @RequestParam(required = false, defaultValue = "false") Boolean includeUsers,
      @Parameter(description = "搜索关键词（部门名称、编码）") @RequestParam(required = false) String keyword) {
    return ApiLocaleResult.success(departmentFacade.getTree(null, status, includeUsers, keyword));
  }

  @Operation(operationId = "getDepartmentPath", summary = "获取部门路径", description = "获取指定部门的路径信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "路径获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/path")
  public ApiLocaleResult<DepartmentPathVo> getPath(
      @Parameter(description = "部门ID") @PathVariable Long id) {
    return ApiLocaleResult.success(departmentFacade.getPath(id));
  }

  @Operation(operationId = "getDepartmentChildren", summary = "获取子部门列表", description = "获取指定部门的子部门列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "子部门列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/children")
  public ApiLocaleResult<List<DepartmentDetailVo>> getChildren(
      @Parameter(description = "部门ID") @PathVariable Long id,
      @Parameter(description = "是否递归获取所有子部门") @RequestParam(required = false, defaultValue = "false") Boolean recursive) {
    return ApiLocaleResult.success(departmentFacade.getChildren(id, recursive));
  }
}
