package cloud.xcan.angus.core.repo.interfaces.access;

import cloud.xcan.angus.core.repo.interfaces.access.facade.AccessFacade;
import cloud.xcan.angus.core.repo.interfaces.access.facade.dto.PermissionCheckDto;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.AccessStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.PermissionCheckResultVo;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.UserPermissionVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AccessPermissions", description = "访问控制 - 权限检查、用户权限查询、访问统计")
@Validated
@RestController
@RequestMapping("/api/v1/repositories/{repositoryId}")
public class AccessPermissionRest {

  @Resource
  private AccessFacade accessFacade;

  @Operation(summary = "检查权限", description = "检查当前用户是否拥有指定权限",
      operationId = "access:checkPermission")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "检查完成")
  })
  @PostMapping("/check-permission")
  public ApiLocaleResult<PermissionCheckResultVo> checkPermission(
      @Parameter(name = "repositoryId", description = "repositoryId") @PathVariable Long repositoryId,
      @Valid @RequestBody PermissionCheckDto dto) {
    return ApiLocaleResult.success(accessFacade.checkPermission(repositoryId, dto));
  }

  @Operation(summary = "查询用户权限", description = "获取当前用户在仓库中的权限列表",
      operationId = "access:getUserPermissions")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/user-permissions")
  public ApiLocaleResult<UserPermissionVo> getUserPermissions(
      @Parameter(name = "repositoryId", description = "repositoryId") @PathVariable Long repositoryId) {
    return ApiLocaleResult.success(accessFacade.getUserPermissions(repositoryId));
  }

  @Operation(summary = "查询访问统计", description = "获取仓库的访问统计数据",
      operationId = "access:getAccessStatistics")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/access-statistics")
  public ApiLocaleResult<AccessStatisticsVo> getAccessStatistics(
      @Parameter(name = "repositoryId", description = "repositoryId") @PathVariable Long repositoryId) {
    return ApiLocaleResult.success(accessFacade.getAccessStatistics(repositoryId));
  }
}
