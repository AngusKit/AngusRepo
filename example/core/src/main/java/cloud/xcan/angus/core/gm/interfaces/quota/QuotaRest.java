package cloud.xcan.angus.core.gm.interfaces.quota;

import cloud.xcan.angus.core.gm.interfaces.quota.facade.QuotaFacade;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.BatchUpdateQuotaLimitsDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.QuotaFindDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.UpdateQuotaDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.UpdateQuotaStatusDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.QuotaStatisticsVo;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.QuotaUsageVo;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.QuotaVo;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Quotas", description = "配额管理 - 资源配额限额设置、使用情况监控和统计分析")
@Validated
@RestController
@RequestMapping("/api/v1/quotas")
public class QuotaRest {

  @Resource
  private QuotaFacade resourceQuotaFacade;

  @Operation(operationId = "updateResourceQuota", summary = "更新资源配额", description = "更新资源配额信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{code}")
  public ApiLocaleResult<QuotaVo> update(
      @Parameter(description = "配额编码") @PathVariable String code,
      @Valid @RequestBody UpdateQuotaDto dto) {
    return ApiLocaleResult.success(resourceQuotaFacade.update(code, dto));
  }

  @Operation(operationId = "batchUpdateQuotaLimits", summary = "批量更新配额限额", description = "批量更新多个资源的配额限额")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/batch-update-limits")
  public ApiLocaleResult<List<QuotaVo>> batchUpdateLimits(
      @Valid @RequestBody BatchUpdateQuotaLimitsDto dto) {
    return ApiLocaleResult.success(resourceQuotaFacade.batchUpdateLimits(dto));
  }

  @Operation(operationId = "updateQuotaStatus", summary = "修改配额状态", description = "启用或禁用资源配额")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{code}/status")
  public ApiLocaleResult<QuotaVo> updateStatus(
      @Parameter(description = "配额编码") @PathVariable String code,
      @Valid @RequestBody UpdateQuotaStatusDto dto) {
    return ApiLocaleResult.success(resourceQuotaFacade.updateStatus(code, dto));
  }

  @Operation(operationId = "getResourceQuota", summary = "查询资源配额详情", description = "根据编码查询资源配额详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{code}")
  public ApiLocaleResult<QuotaVo> getByCode(
      @Parameter(description = "配额编码") @PathVariable String code) {
    return ApiLocaleResult.success(resourceQuotaFacade.getByCode(code));
  }

  @Operation(operationId = "listResourceQuotas", summary = "查询资源配额列表", description = "分页查询资源配额列表，支持搜索和筛选")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<List<QuotaVo>> list(@Valid @ParameterObject QuotaFindDto dto) {
    return ApiLocaleResult.success(resourceQuotaFacade.list(dto));
  }

  @Operation(operationId = "getQuotaStatistics", summary = "查询配额统计信息", description = "查询资源配额的统计信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<QuotaStatisticsVo> getStatistics() {
    return ApiLocaleResult.success(resourceQuotaFacade.getStatistics());
  }

  @Operation(operationId = "getQuotaUsage", summary = "查询单个资源的使用情况", description = "查询指定资源的实时使用情况")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{code}/usage")
  public ApiLocaleResult<QuotaUsageVo> getUsage(
      @Parameter(description = "配额编码") @PathVariable String code) {
    return ApiLocaleResult.success(resourceQuotaFacade.getUsage(code));
  }
}
