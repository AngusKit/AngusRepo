package cloud.xcan.angus.core.repo.interfaces.security;

import cloud.xcan.angus.core.repo.interfaces.security.facade.ScanTaskFacade;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanTaskCreateDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanTaskFindDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanTaskUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.vo.ScanStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.security.facade.vo.ScanTaskDetailVo;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SecurityScans", description = "安全扫描 - 扫描任务管理、统计")
@Validated
@RestController
@RequestMapping("/api/v1/security/scans")
public class ScanTaskRest {

  @Resource
  private ScanTaskFacade scanTaskFacade;

  @Operation(summary = "创建扫描任务", description = "创建新的安全扫描任务", operationId = "scan:create")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "创建成功")})
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiLocaleResult<ScanTaskDetailVo> create(@Valid @RequestBody ScanTaskCreateDto dto) {
    return ApiLocaleResult.success(scanTaskFacade.create(dto));
  }

  @Operation(summary = "更新扫描任务", description = "更新扫描任务信息", operationId = "scan:update")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "更新成功")})
  @PutMapping("/{id}")
  public ApiLocaleResult<ScanTaskDetailVo> update(@Parameter(name = "id", description = "id") @PathVariable String id, @Valid @RequestBody ScanTaskUpdateDto dto) {
    return ApiLocaleResult.success(scanTaskFacade.update(id, dto));
  }

  @Operation(summary = "取消扫描任务", description = "取消正在执行的扫描任务", operationId = "scan:cancel")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "取消成功")})
  @PutMapping("/{id}/cancel")
  public ApiLocaleResult<?> cancel(@Parameter(name = "id", description = "id") @PathVariable String id) {
    scanTaskFacade.cancel(id);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "删除扫描任务", description = "删除扫描任务", operationId = "scan:delete")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "删除成功")})
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@Parameter(name = "id", description = "id") @PathVariable String id) {
    scanTaskFacade.delete(id);
  }

  @Operation(summary = "查询扫描详情", description = "获取扫描任务详细信息", operationId = "scan:getById")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功"),
      @ApiResponse(responseCode = "404", description = "任务不存在")
  })
  @GetMapping("/{id}")
  public ApiLocaleResult<ScanTaskDetailVo> getById(@Parameter(name = "id", description = "id") @PathVariable String id) {
    return ApiLocaleResult.success(scanTaskFacade.getById(id));
  }

  @Operation(summary = "查询扫描列表", description = "分页查询扫描任务列表", operationId = "scan:list")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping
  public ApiLocaleResult<PageResult<ScanTaskDetailVo>> list(@Valid @ParameterObject ScanTaskFindDto dto) {
    return ApiLocaleResult.success(scanTaskFacade.list(dto));
  }

  @Operation(summary = "查询扫描统计", description = "获取扫描统计数据", operationId = "scan:statistics")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping("/statistics")
  public ApiLocaleResult<ScanStatisticsVo> getStatistics() {
    return ApiLocaleResult.success(scanTaskFacade.getStatistics());
  }
}
