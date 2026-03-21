package cloud.xcan.angus.core.gm.interfaces.interfaces;

import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.InterfaceRequestLogFacade;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceRequestLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceRequestLogStatisticsDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceRequestLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceRequestLogListVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.InterfaceRequestLogStatisticsVo;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "InterfaceRequestLog", description = "API请求日志管理 - API请求日志的查询、统计等功能")
@Validated
@RestController
@RequestMapping("/api/v1/interface/logs")
public class InterfaceRequestLogRest {

  @Resource
  private InterfaceRequestLogFacade interfaceRequestLogFacade;

  @Operation(operationId = "getApiRequestLogDetail", summary = "获取API请求日志详情",
      description = "获取指定ID的API请求日志详情，包含完整的请求和响应信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "日志详情获取成功"),
      @ApiResponse(responseCode = "404", description = "日志不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<InterfaceRequestLogDetailVo> getDetail(
      @Parameter(description = "日志ID") @PathVariable Long id) {
    return ApiLocaleResult.success(interfaceRequestLogFacade.getDetail(id));
  }

  @Operation(operationId = "getApiRequestLogList", summary = "获取API请求日志列表",
      description = "分页查询API请求日志列表，支持多条件筛选")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "日志列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<InterfaceRequestLogListVo>> list(
      @Valid @ParameterObject InterfaceRequestLogFindDto dto) {
    return ApiLocaleResult.success(interfaceRequestLogFacade.list(dto));
  }

  @Operation(operationId = "getApiRequestLogStatistics", summary = "获取API请求日志统计数据",
      description = "获取API请求日志的统计数据，包括成功率、平均响应时间、各方法统计等")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "统计数据获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<InterfaceRequestLogStatisticsVo> getStatistics(
      @Valid @ParameterObject InterfaceRequestLogStatisticsDto dto) {
    return ApiLocaleResult.success(interfaceRequestLogFacade.getStatistics(dto));
  }
}
