package cloud.xcan.angus.core.gm.interfaces.group;

import cloud.xcan.angus.core.gm.interfaces.group.facade.GroupUserFacade;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUserAddDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUserRemoveDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUserTransferDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupUserAddVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupUserTransferVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupUserVo;
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

@Tag(name = "GroupUser", description = "组用户管理 - 组用户的添加、移除等功能")
@Validated
@RestController
@RequestMapping("/api/v1/groups/{groupId}/users")
public class GroupUserRest {

  @Resource
  private GroupUserFacade groupUserFacade;

  @Operation(operationId = "addGroupUsers", summary = "添加组用户", description = "向组中添加用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "添加成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping
  public ApiLocaleResult<GroupUserAddVo> addUsers(
      @Parameter(description = "组ID") @PathVariable Long groupId,
      @Valid @RequestBody GroupUserAddDto dto) {
    return ApiLocaleResult.success(groupUserFacade.addUsers(groupId, dto));
  }

  @Operation(operationId = "transferGroupUsers", summary = "转移组用户", description = "将用户从当前组转移到目标组")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "用户转移成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/transfer")
  public ApiLocaleResult<GroupUserTransferVo> transferUsers(
      @Parameter(description = "源组ID") @PathVariable Long groupId,
      @Valid @RequestBody GroupUserTransferDto dto) {
    return ApiLocaleResult.success(groupUserFacade.transferUsers(groupId, dto));
  }

  @Operation(operationId = "removeGroupUser", summary = "移除组用户", description = "从组中移除单个用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "移除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{userId}")
  public void removeUser(
      @Parameter(description = "组ID") @PathVariable Long groupId,
      @Parameter(description = "用户ID") @PathVariable Long userId) {
    groupUserFacade.removeUser(groupId, userId);
  }

  @Operation(operationId = "removeGroupUsers", summary = "批量移除组用户", description = "从组中批量移除用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "移除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping
  public void removeUsers(
      @Parameter(description = "组ID") @PathVariable Long groupId,
      @Valid @RequestBody GroupUserRemoveDto dto) {
    groupUserFacade.removeUsers(groupId, dto);
  }

  @Operation(operationId = "getGroupUsers", summary = "获取组用户列表", description = "获取指定组的用户列表，支持分页")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "用户列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<GroupUserVo>> listUsers(
      @Parameter(description = "组ID") @PathVariable Long groupId,
      @Valid @ParameterObject GroupUserFindDto dto) {
    return ApiLocaleResult.success(groupUserFacade.listUsers(groupId, dto));
  }

  @Operation(operationId = "getUsersNotInGroup", summary = "获取未加入组的用户列表", description = "获取未加入指定组的用户列表，支持分页")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "用户列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/not-in-group")
  public ApiLocaleResult<PageResult<GroupUserVo>> listUsersNotInGroup(
      @Parameter(description = "组ID") @PathVariable Long groupId,
      @Valid @ParameterObject GroupUserFindDto dto) {
    return ApiLocaleResult.success(groupUserFacade.listUsersNotInGroup(groupId, dto));
  }

}
