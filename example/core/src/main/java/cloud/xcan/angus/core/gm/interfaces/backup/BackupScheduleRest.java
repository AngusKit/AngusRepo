package cloud.xcan.angus.core.gm.interfaces.backup;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.BackupScheduleFacade;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.ScheduleCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.ScheduleUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.ScheduleDetailVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.ScheduleRunVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.ScheduleStatusVo;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "BackupSchedule", description = "备份计划管理 - 备份计划的创建、更新、启用/禁用、执行和删除")
@Validated
@RestController
@RequestMapping("/api/v1/backup/schedules")
public class BackupScheduleRest {

  @Resource
  private BackupScheduleFacade backupScheduleFacade;

  @Operation(operationId = "createSchedule", summary = "创建备份计划", description = "创建一个新的备份计划，指定计划名称、类型、频率等信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "备份计划创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<ScheduleDetailVo> createSchedule(
      @Valid @RequestBody ScheduleCreateDto dto) {
    return ApiLocaleResult.success(backupScheduleFacade.createSchedule(dto));
  }

  @Operation(operationId = "updateSchedule", summary = "更新备份计划", description = "根据计划ID更新指定备份计划的信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "备份计划更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}")
  public ApiLocaleResult<ScheduleDetailVo> updateSchedule(
      @Parameter(description = "计划ID") @PathVariable Long id,
      @Valid @RequestBody ScheduleUpdateDto dto) {
    return ApiLocaleResult.success(backupScheduleFacade.updateSchedule(id, dto));
  }

  @Operation(operationId = "updateScheduleStatus", summary = "启用/禁用备份计划", description = "根据计划ID更新备份计划的启用/禁用状态")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "备份计划状态更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}/status")
  public ApiLocaleResult<ScheduleStatusVo> updateScheduleStatus(
      @Parameter(description = "计划ID") @PathVariable Long id,
      @Valid @RequestBody EnabledStatusUpdateDto dto) {
    return ApiLocaleResult.success(backupScheduleFacade.updateScheduleStatus(id, dto));
  }

  @Operation(operationId = "deleteSchedule", summary = "删除备份计划", description = "根据计划ID删除指定的备份计划")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "备份计划删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void deleteSchedule(
      @Parameter(description = "计划ID") @PathVariable Long id) {
    backupScheduleFacade.deleteSchedule(id);
  }

  @Operation(operationId = "runSchedule", summary = "立即执行备份计划", description = "根据计划ID立即执行指定的备份计划")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "备份任务启动成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/run")
  public ApiLocaleResult<ScheduleRunVo> runSchedule(
      @Parameter(description = "计划ID") @PathVariable Long id) {
    return ApiLocaleResult.success(backupScheduleFacade.runSchedule(id));
  }

  @Operation(operationId = "getBackupSchedules", summary = "获取备份计划列表", description = "获取所有备份计划的列表信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "备份计划列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<List<ScheduleDetailVo>> listSchedules() {
    return ApiLocaleResult.success(backupScheduleFacade.listSchedules());
  }
}
