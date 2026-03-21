package cloud.xcan.angus.core.gm.interfaces.log;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.core.gm.interfaces.log.facade.LogRetentionConfigFacade;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.LogRetentionConfigCleanupDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.LogRetentionConfigFindDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.LogRetentionConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.LogRetentionConfigCleanupVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.LogRetentionConfigDetailVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "LogRetentionConfig", description = "日志清理配置管理 - 日志保留策略管理")
@Validated
@RestController
@RequestMapping("/api/v1/logs/retention-configs")
public class LogRetentionConfigRest {

  @Resource
  private LogRetentionConfigFacade logRetentionConfigFacade;

  @Operation(operationId = "updateLogRetentionConfig", summary = "更新日志清理配置",
      description = "更新指定应用的日志清理配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "配置更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}")
  public ApiLocaleResult<LogRetentionConfigDetailVo> update(
      @Parameter(description = "配置ID（应用ID）", required = true) @PathVariable("id") Long id,
      @Valid @RequestBody LogRetentionConfigUpdateDto dto) {
    return ApiLocaleResult.success(logRetentionConfigFacade.update(id, dto));
  }

  @Operation(operationId = "batchUpdateLogRetentionConfig", summary = "批量更新日志清理配置",
      description = "批量更新多个应用的日志清理配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "批量更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/batch")
  public ApiLocaleResult<List<LogRetentionConfigDetailVo>> batchUpdate(
      @Valid @RequestBody List<LogRetentionConfigUpdateDto> dto) {
    return ApiLocaleResult.success(logRetentionConfigFacade.batchUpdate(dto));
  }

  @Operation(operationId = "findLogRetentionConfigList", summary = "查询日志清理配置列表",
      description = "查询日志清理配置列表，支持筛选和排序")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<List<LogRetentionConfigDetailVo>> findList(
      @ParameterObject LogRetentionConfigFindDto dto) {
    return ApiLocaleResult.success(logRetentionConfigFacade.findList(dto));
  }

  @Operation(operationId = "cleanupLogRetentionConfig", summary = "执行日志清理",
      description = "立即执行指定配置的日志清理任务（不等待定时任务）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "清理任务执行成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/cleanup")
  public ApiLocaleResult<LogRetentionConfigCleanupVo> cleanup(
      @Parameter(description = "配置ID（应用ID）", required = true) @PathVariable("id") Long id,
      @Valid @RequestBody(required = false) LogRetentionConfigCleanupDto dto) {
    return ApiLocaleResult.success(logRetentionConfigFacade.cleanup(id,
        nullSafe(dto, new LogRetentionConfigCleanupDto())));
  }
}
