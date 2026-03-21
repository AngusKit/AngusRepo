package cloud.xcan.angus.core.gm.interfaces.group;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.GroupFacade;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupOwnerUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupOwnerUpdateVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupOwnerVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupStatsVo;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Group", description = "组管理 - 组的创建、管理等功能")
@Validated
@RestController
@RequestMapping("/api/v1/groups")
public class GroupRest {

  @Resource
  private GroupFacade groupFacade;

  @Operation(operationId = "createGroup", summary = "创建组", description = "创建新组")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "组创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<GroupDetailVo> create(
      @Valid @RequestBody GroupCreateDto dto) {
    return ApiLocaleResult.success(groupFacade.create(dto));
  }

  @Operation(operationId = "updateGroup", summary = "更新组", description = "更新组基本信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}")
  public ApiLocaleResult<GroupDetailVo> update(
      @Parameter(description = "组ID") @PathVariable Long id,
      @Valid @RequestBody GroupUpdateDto dto) {
    return ApiLocaleResult.success(groupFacade.update(id, dto));
  }

  @Operation(operationId = "updateGroupStatus", summary = "禁用/激活部门", description = "修改组状态")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "状态更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}/status")
  public ApiLocaleResult<GroupDetailVo> updateStatus(
      @Parameter(description = "组ID") @PathVariable Long id,
      @Valid @RequestBody EnabledStatusUpdateDto dto) {
    return ApiLocaleResult.success(groupFacade.updateStatus(id, dto));
  }

  @Operation(operationId = "updateGroupOwner", summary = "设置组负责人", description = "更新组的负责人")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "负责人设置成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}/owner")
  public ApiLocaleResult<GroupOwnerUpdateVo> updateOwner(
      @Parameter(description = "组ID") @PathVariable Long id,
      @Valid @RequestBody GroupOwnerUpdateDto dto) {
    return ApiLocaleResult.success(groupFacade.updateOwner(id, dto));
  }

  @Operation(operationId = "deleteGroup", summary = "删除组", description = "删除指定组")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(description = "组ID") @PathVariable Long id) {
    groupFacade.delete(id);
  }

  @Operation(operationId = "getGroupDetail", summary = "获取组详情",
      description = "获取指定组的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "组详情获取成功"),
      @ApiResponse(responseCode = "404", description = "组不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<GroupDetailVo> getDetail(
      @Parameter(description = "组ID") @PathVariable Long id) {
    return ApiLocaleResult.success(groupFacade.getDetail(id));
  }

  @Operation(operationId = "getGroupList", summary = "获取组列表",
      description = "获取组列表，支持分页、搜索和筛选")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "组列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<GroupDetailVo>> list(
      @Valid @ParameterObject GroupFindDto dto) {
    return ApiLocaleResult.success(groupFacade.list(dto));
  }

  @Operation(operationId = "getGroupStats", summary = "获取组统计数据",
      description = "获取组统计数据，包括总数、类型分布等")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "统计数据获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<GroupStatsVo> getStats() {
    return ApiLocaleResult.success(groupFacade.getStats());
  }

  @Operation(operationId = "getGroupsByUser", summary = "获取用户所在的组列表",
      description = "获取指定用户所在的所有组")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "组列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/user/{userId}")
  public ApiLocaleResult<List<GroupOwnerVo>> getGroupsByUser(
      @Parameter(description = "用户ID") @PathVariable Long userId) {
    return ApiLocaleResult.success(groupFacade.getGroupsByUser(userId));
  }
}
