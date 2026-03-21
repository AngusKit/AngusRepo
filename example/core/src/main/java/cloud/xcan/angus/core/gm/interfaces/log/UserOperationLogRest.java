package cloud.xcan.angus.core.gm.interfaces.log;

import cloud.xcan.angus.core.gm.interfaces.log.facade.UserOperationLogFacade;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.UserOperationLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.UserOperationLogStatisticsDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.UserOperationLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.UserOperationLogStatisticsVo;
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

@Tag(name = "UserOperationLog", description = "用户操作日志管理 - 用户操作日志的查询、导出、统计等功能")
@Validated
@RestController
@RequestMapping("/api/v1/logs/user-operation")
public class UserOperationLogRest {

  @Resource
  private UserOperationLogFacade userOperationLogFacade;

  @Operation(operationId = "getUserOperationLogDetail", summary = "获取用户操作日志详情",
      description = "获取指定ID的用户操作日志详情")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "日志详情获取成功"),
      @ApiResponse(responseCode = "404", description = "日志不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<UserOperationLogDetailVo> getDetail(
      @Parameter(description = "日志ID") @PathVariable Long id) {
    return ApiLocaleResult.success(userOperationLogFacade.getDetail(id));
  }

  @Operation(operationId = "getUserOperationLogList", summary = "获取用户操作日志列表",
      description = "分页查询用户操作日志列表，支持多条件筛选")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "日志列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<UserOperationLogDetailVo>> list(
      @Valid @ParameterObject UserOperationLogFindDto dto) {
    return ApiLocaleResult.success(userOperationLogFacade.list(dto));
  }

  @Operation(operationId = "getUserOperationLogStatistics", summary = "获取用户操作日志统计数据",
      description = "获取用户操作日志的统计数据，包括各操作类型统计、资源类型统计、操作最频繁的用户等")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "统计数据获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<UserOperationLogStatisticsVo> getStatistics(
      @Valid @ParameterObject UserOperationLogStatisticsDto dto) {
    return ApiLocaleResult.success(userOperationLogFacade.getStatistics(dto));
  }
}
