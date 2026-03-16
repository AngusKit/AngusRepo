package cloud.xcan.angus.core.repo.interfaces.activitylog;

import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.ActivityLogFacade;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.dto.ActivityLogBatchDeleteDto;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.dto.ActivityLogCreateDto;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.dto.ActivityLogExportDto;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.dto.ActivityLogFindDto;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.vo.ActivityLogStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.vo.ActivityLogVo;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.vo.ActivityUserListVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
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

/**
 * 活动日志REST控制器
 */
@Tag(name = "Activity Log", description = "活动日志 - 日志记录、查询、导出等功能")
@Validated
@RestController
@RequestMapping("/api/v1/activity-logs")
public class ActivityLogRest {

  @Resource
  private ActivityLogFacade activityLogFacade;

  @Operation(summary = "创建活动日志", description = "创建活动日志记录（通常由系统自动记录）",
      operationId = "activity-log:create")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "活动日志创建成功"),
      @ApiResponse(responseCode = "400", description = "请求参数错误")
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiLocaleResult<ActivityLogVo> create(@Valid @RequestBody ActivityLogCreateDto dto) {
    return ApiLocaleResult.success(activityLogFacade.create(dto));
  }

  @Operation(summary = "查询活动日志详情", description = "查询单条活动日志详情",
      operationId = "activity-log:getById")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功"),
      @ApiResponse(responseCode = "404", description = "日志不存在")
  })
  @GetMapping("/{id}")
  public ApiLocaleResult<ActivityLogVo> getById(@Parameter(name = "id", description = "活动日志ID") @PathVariable String id) {
    return ApiLocaleResult.success(activityLogFacade.getById(id));
  }

  @Operation(summary = "查询活动日志列表", description = "分页查询活动日志列表，支持多维度筛选",
      operationId = "activity-log:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping
  public ApiLocaleResult<PageResult<ActivityLogVo>> list(
      @Valid @ParameterObject ActivityLogFindDto dto) {
    return ApiLocaleResult.success(activityLogFacade.list(dto));
  }

  @Operation(summary = "查询活动日志统计", description = "查询活动日志统计信息",
      operationId = "activity-log:getStatistics")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/statistics")
  public ApiLocaleResult<ActivityLogStatisticsVo> getStatistics(
      @Valid @ParameterObject ActivityLogFindDto dto) {
    return ApiLocaleResult.success(activityLogFacade.getStatistics(dto));
  }

  @Operation(summary = "导出活动日志", description = "导出活动日志为CSV或Excel文件",
      operationId = "activity-log:export")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "导出成功")
  })
  @GetMapping("/export")
  public void export(@Valid @ParameterObject ActivityLogExportDto dto,
      HttpServletResponse response) throws IOException {
    activityLogFacade.export(dto, response);
  }

  @Operation(summary = "删除活动日志", description = "删除单条活动日志", operationId = "activity-log:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "活动日志删除成功"),
      @ApiResponse(responseCode = "404", description = "日志不存在")
  })
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@Parameter(name = "id", description = "活动日志ID") @PathVariable String id) {
    activityLogFacade.delete(id);
  }

  @Operation(summary = "批量删除活动日志", description = "批量删除活动日志",
      operationId = "activity-log:deleteBatch")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "批量删除成功")
  })
  @DeleteMapping("/batch")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBatch(@Valid @RequestBody ActivityLogBatchDeleteDto dto) {
    activityLogFacade.deleteBatch(dto);
  }

  @Operation(summary = "获取唯一用户列表", description = "获取所有操作过系统的唯一用户列表（用于筛选）",
      operationId = "activity-log:getUniqueUsers")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/users")
  public ApiLocaleResult<ActivityUserListVo> getUniqueUsers() {
    return ApiLocaleResult.success(activityLogFacade.getUniqueUsers());
  }
}
