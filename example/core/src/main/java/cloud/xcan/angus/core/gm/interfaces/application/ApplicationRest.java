package cloud.xcan.angus.core.gm.interfaces.application;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.ApplicationFacade;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationFindDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationListVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationStatsVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagListVo;
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

@Tag(name = "Application", description = "应用管理 - 应用的增删改查、标签管理")
@Validated
@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationRest {

  @Resource
  private ApplicationFacade applicationFacade;

  @Operation(operationId = "createApplication", summary = "创建应用", description = "创建新应用")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "应用创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<ApplicationDetailVo> create(
      @Valid @RequestBody ApplicationCreateDto dto) {
    return ApiLocaleResult.success(applicationFacade.create(dto));
  }

  @Operation(operationId = "updateApplication", summary = "更新应用", description = "更新应用基本信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}")
  public ApiLocaleResult<ApplicationDetailVo> update(
      @Parameter(description = "应用ID") @PathVariable Long id,
      @Valid @RequestBody ApplicationUpdateDto dto) {
    return ApiLocaleResult.success(applicationFacade.update(id, dto));
  }

  @Operation(operationId = "updateEnabledStatus", summary = "启用/禁用应用", description = "修改应用状态")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "状态更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}/status")
  public ApiLocaleResult<ApplicationDetailVo> updateStatus(
      @Parameter(description = "应用ID") @PathVariable Long id,
      @Valid @RequestBody EnabledStatusUpdateDto dto) {
    return ApiLocaleResult.success(applicationFacade.updateStatus(id, dto));
  }

  @Operation(operationId = "deleteApplication", summary = "删除应用", description = "删除指定应用")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(description = "应用ID") @PathVariable Long id) {
    applicationFacade.delete(id);
  }

  @Operation(operationId = "getApplicationDetail", summary = "获取应用详情",
      description = "获取指定应用的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "应用详情获取成功"),
      @ApiResponse(responseCode = "404", description = "应用不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<ApplicationDetailVo> getDetail(
      @Parameter(description = "应用ID") @PathVariable Long id) {
    return ApiLocaleResult.success(applicationFacade.getDetail(id));
  }

  @Operation(operationId = "getApplicationList", summary = "获取应用列表",
      description = "获取应用列表，支持分页、搜索和筛选")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "应用列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<ApplicationListVo>> find(
      @Valid @ParameterObject ApplicationFindDto dto) {
    return ApiLocaleResult.success(applicationFacade.find(dto));
  }

  @Operation(operationId = "getApplicationStats", summary = "获取应用统计数据",
      description = "获取应用统计数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "统计数据获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<ApplicationStatsVo> getStats() {
    return ApiLocaleResult.success(applicationFacade.getStats());
  }

  @Operation(operationId = "getAvailableTags", summary = "获取可用标签列表",
      description = "获取应用可用的标签列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "标签列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/available-tags")
  public ApiLocaleResult<List<TagListVo>> getAvailableTags() {
    return ApiLocaleResult.success(applicationFacade.getAvailableTags());
  }
}
