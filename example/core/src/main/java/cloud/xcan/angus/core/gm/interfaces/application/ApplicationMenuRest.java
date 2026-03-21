package cloud.xcan.angus.core.gm.interfaces.application;

import cloud.xcan.angus.core.gm.interfaces.application.facade.ApplicationMenuFacade;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationMenuCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationMenuUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationMenuVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
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

@Tag(name = "ApplicationMenu", description = "应用菜单管理 - 应用菜单的增删改查、排序")
@Validated
@RestController
@RequestMapping("/api/v1/applications/{appId}/menus")
public class ApplicationMenuRest {

  @Resource
  private ApplicationMenuFacade applicationMenuFacade;

  @Operation(operationId = "createApplicationMenu", summary = "创建应用菜单", description = "为应用创建新菜单")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "菜单创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<ApplicationMenuVo> createMenu(
      @Parameter(description = "应用ID") @PathVariable Long appId,
      @Valid @RequestBody ApplicationMenuCreateDto dto) {
    return ApiLocaleResult.success(applicationMenuFacade.createMenu(appId, dto));
  }

  @Operation(operationId = "updateApplicationMenu", summary = "更新应用菜单", description = "更新应用菜单基本信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "菜单更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{menuId}")
  public ApiLocaleResult<ApplicationMenuVo> updateMenu(
      @Parameter(description = "应用ID") @PathVariable Long appId,
      @Parameter(description = "菜单ID") @PathVariable Long menuId,
      @Valid @RequestBody ApplicationMenuUpdateDto dto) {
    return ApiLocaleResult.success(applicationMenuFacade.updateMenu(appId, menuId, dto));
  }

  @Operation(operationId = "deleteApplicationMenu", summary = "删除应用菜单", description = "删除应用的菜单")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "菜单删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{menuId}")
  public void deleteMenu(
      @Parameter(description = "应用ID") @PathVariable Long appId,
      @Parameter(description = "菜单ID") @PathVariable Long menuId) {
    applicationMenuFacade.deleteMenu(appId, menuId);
  }

  @Operation(operationId = "getApplicationMenus", summary = "获取应用菜单列表",
      description = "获取指定应用的菜单树")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "菜单列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<List<ApplicationMenuVo>> getMenus(
      @Parameter(description = "应用ID") @PathVariable Long appId) {
    return ApiLocaleResult.success(applicationMenuFacade.getMenus(appId));
  }
}
