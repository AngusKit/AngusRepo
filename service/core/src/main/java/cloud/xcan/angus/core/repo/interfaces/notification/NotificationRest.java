package cloud.xcan.angus.core.repo.interfaces.notification;

import cloud.xcan.angus.core.repo.interfaces.notification.facade.NotificationFacade;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.dto.NotificationBatchDeleteDto;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.dto.NotificationBatchReadDto;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.dto.NotificationCreateDto;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.dto.NotificationFindDto;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.dto.NotificationUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.vo.NotificationDetailVo;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.vo.NotificationStatisticsVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Notifications", description = "通知管理 - 通知的增删改查、已读/星标/归档管理")
@Validated
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationRest {

  @Resource
  private NotificationFacade notificationFacade;

  @Operation(summary = "创建通知", operationId = "notification:create")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "创建成功")})
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiLocaleResult<NotificationDetailVo> create(@Valid @RequestBody NotificationCreateDto dto) {
    return ApiLocaleResult.success(notificationFacade.create(dto));
  }

  @Operation(summary = "更新通知", operationId = "notification:update")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "更新成功")})
  @PutMapping("/{id}")
  public ApiLocaleResult<NotificationDetailVo> update(@PathVariable String id, @Valid @RequestBody NotificationUpdateDto dto) {
    return ApiLocaleResult.success(notificationFacade.update(id, dto));
  }

  @Operation(summary = "标记已读", operationId = "notification:markAsRead")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "操作成功")})
  @PutMapping("/{id}/read")
  public ApiLocaleResult<?> markAsRead(@PathVariable String id) {
    notificationFacade.markAsRead(id);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "删除通知", operationId = "notification:delete")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "删除成功")})
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String id) {
    notificationFacade.delete(id);
  }

  @Operation(summary = "查询通知详情", operationId = "notification:getById")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功"),
      @ApiResponse(responseCode = "404", description = "通知不存在")
  })
  @GetMapping("/{id}")
  public ApiLocaleResult<NotificationDetailVo> getById(@PathVariable String id) {
    return ApiLocaleResult.success(notificationFacade.getById(id));
  }

  @Operation(summary = "查询通知列表", operationId = "notification:list")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping
  public ApiLocaleResult<PageResult<NotificationDetailVo>> list(@Valid @ParameterObject NotificationFindDto dto) {
    return ApiLocaleResult.success(notificationFacade.list(dto));
  }

  @Operation(summary = "查询通知统计", operationId = "notification:statistics")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping("/statistics")
  public ApiLocaleResult<NotificationStatisticsVo> getStatistics() {
    return ApiLocaleResult.success(notificationFacade.getStatistics());
  }

  @Operation(summary = "批量标记已读", operationId = "notification:batchRead")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "操作成功")})
  @PutMapping("/batch-read")
  public ApiLocaleResult<?> markBatchAsRead(@Valid @RequestBody NotificationBatchReadDto dto) {
    notificationFacade.markBatchAsRead(dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "批量删除通知", operationId = "notification:batchDelete")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "删除成功")})
  @DeleteMapping("/batch")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBatch(@Valid @RequestBody NotificationBatchDeleteDto dto) {
    notificationFacade.deleteBatch(dto);
  }

  @Operation(summary = "添加星标", operationId = "notification:addStar")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "操作成功")})
  @PutMapping("/{id}/star")
  public ApiLocaleResult<?> addStar(@PathVariable String id) {
    notificationFacade.addStar(id);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "移除星标", operationId = "notification:removeStar")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "操作成功")})
  @DeleteMapping("/{id}/star")
  public ApiLocaleResult<?> removeStar(@PathVariable String id) {
    notificationFacade.removeStar(id);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "归档通知", operationId = "notification:archive")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "操作成功")})
  @PutMapping("/{id}/archive")
  public ApiLocaleResult<?> archive(@PathVariable String id) {
    notificationFacade.archive(id);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "取消归档", operationId = "notification:unarchive")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "操作成功")})
  @DeleteMapping("/{id}/archive")
  public ApiLocaleResult<?> unarchive(@PathVariable String id) {
    notificationFacade.unarchive(id);
    return ApiLocaleResult.success();
  }
}
