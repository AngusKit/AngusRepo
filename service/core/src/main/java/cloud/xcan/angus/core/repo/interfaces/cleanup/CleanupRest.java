package cloud.xcan.angus.core.repo.interfaces.cleanup;

import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.CleanupFacade;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto.CleanupPolicyBatchDeleteDto;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto.CleanupPolicyCreateDto;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto.CleanupPolicyFindDto;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto.CleanupPolicyUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.vo.CleanupExecutionVo;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.vo.CleanupPolicyDetailVo;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.vo.CleanupStatisticsVo;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CleanupPolicies", description = "清理策略管理 - 清理策略的创建、更新、删除、执行、取消、统计")
@Validated
@RestController
@RequestMapping("/api/v1/cleanup-policies")
public class CleanupRest {

  @Resource
  private CleanupFacade cleanupFacade;

  @Operation(summary = "创建清理策略", description = "创建新的清理策略",
      operationId = "cleanup:create")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "清理策略创建成功")
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiLocaleResult<CleanupPolicyDetailVo> create(
      @Valid @RequestBody CleanupPolicyCreateDto dto) {
    return ApiLocaleResult.success(cleanupFacade.create(dto));
  }

  @Operation(summary = "更新清理策略", description = "更新清理策略基本信息",
      operationId = "cleanup:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @PutMapping("/{id}")
  public ApiLocaleResult<CleanupPolicyDetailVo> update(
      @Parameter(name = "id", description = "id") @PathVariable String id, @Valid @RequestBody CleanupPolicyUpdateDto dto) {
    return ApiLocaleResult.success(cleanupFacade.update(id, dto));
  }

  @Operation(summary = "删除清理策略", description = "删除指定清理策略",
      operationId = "cleanup:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@Parameter(name = "id", description = "id") @PathVariable String id) {
    cleanupFacade.delete(id);
  }

  @Operation(summary = "批量删除清理策略", description = "批量删除清理策略",
      operationId = "cleanup:deleteBatch")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "批量删除成功")
  })
  @DeleteMapping("/batch")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBatch(@Valid @RequestBody CleanupPolicyBatchDeleteDto dto) {
    cleanupFacade.deleteBatch(dto);
  }

  @Operation(summary = "启用/禁用清理策略", description = "启用或禁用指定清理策略",
      operationId = "cleanup:updateEnabled")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "操作成功")
  })
  @PutMapping("/{id}/enabled")
  public ApiLocaleResult<?> updateEnabled(
      @Parameter(name = "id", description = "id") @PathVariable String id, @RequestParam Boolean enabled) {
    cleanupFacade.updateEnabled(id, enabled);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "获取清理策略详情", description = "获取清理策略详细信息",
      operationId = "cleanup:getById")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功"),
      @ApiResponse(responseCode = "404", description = "清理策略不存在")
  })
  @GetMapping("/{id}")
  public ApiLocaleResult<CleanupPolicyDetailVo> getById(@Parameter(name = "id", description = "id") @PathVariable String id) {
    return ApiLocaleResult.success(cleanupFacade.getById(id));
  }

  @Operation(summary = "查询清理策略列表", description = "分页查询清理策略列表，支持多维度筛选",
      operationId = "cleanup:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping
  public ApiLocaleResult<PageResult<CleanupPolicyDetailVo>> list(
      @Valid @ParameterObject CleanupPolicyFindDto dto) {
    return ApiLocaleResult.success(cleanupFacade.list(dto));
  }

  @Operation(summary = "获取清理统计", description = "获取清理统计数据",
      operationId = "cleanup:getStatistics")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/statistics")
  public ApiLocaleResult<CleanupStatisticsVo> getStatistics() {
    return ApiLocaleResult.success(cleanupFacade.getStatistics());
  }

  @Operation(summary = "手动执行清理策略", description = "手动触发指定清理策略执行",
      operationId = "cleanup:execute")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "执行已启动")
  })
  @PostMapping("/{id}/execute")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiLocaleResult<CleanupExecutionVo> execute(@Parameter(name = "id", description = "id") @PathVariable String id) {
    return ApiLocaleResult.success(cleanupFacade.execute(id));
  }

  @Operation(summary = "取消执行", description = "取消正在执行的清理任务",
      operationId = "cleanup:cancelExecution")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "取消成功")
  })
  @PutMapping("/executions/{executionId}/cancel")
  public ApiLocaleResult<?> cancelExecution(@Parameter(name = "executionId", description = "executionId") @PathVariable String executionId) {
    cleanupFacade.cancelExecution(executionId);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "获取执行历史", description = "获取指定清理策略的执行历史记录",
      operationId = "cleanup:getExecutions")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/{id}/executions")
  public ApiLocaleResult<List<CleanupExecutionVo>> getExecutions(@Parameter(name = "id", description = "id") @PathVariable String id) {
    return ApiLocaleResult.success(cleanupFacade.getExecutions(id));
  }
}
