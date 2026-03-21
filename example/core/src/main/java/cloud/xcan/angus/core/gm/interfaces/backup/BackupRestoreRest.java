package cloud.xcan.angus.core.gm.interfaces.backup;

import cloud.xcan.angus.core.gm.interfaces.backup.facade.RestoreTaskFacade;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupValidateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.RestoreCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.RestoreFindDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupValidationVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.RestoreTaskDetailVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.RestoreTaskListVo;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "BackupRestore", description = "数据恢复管理 - 备份恢复任务管理")
@Validated
@RestController
@RequestMapping("/api/v1/backup/restores")
public class BackupRestoreRest {

  @Resource
  private RestoreTaskFacade restoreTaskFacade;

  @Operation(operationId = "createRestoreTask", summary = "创建恢复任务",
      description = "创建数据恢复任务，从备份文件恢复系统数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "数据恢复任务已创建")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<RestoreTaskDetailVo> create(
      @Valid @RequestBody RestoreCreateDto dto) {
    return ApiLocaleResult.success(restoreTaskFacade.create(dto));
  }

  @Operation(operationId = "getRestoreTaskDetail", summary = "查询恢复任务详情",
      description = "根据任务ID查询恢复任务详细信息和进度")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "恢复任务详情获取成功"),
      @ApiResponse(responseCode = "404", description = "恢复任务不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<RestoreTaskDetailVo> getDetail(
      @Parameter(description = "恢复任务ID") @PathVariable Long id) {
    return ApiLocaleResult.success(restoreTaskFacade.getDetail(id));
  }

  @Operation(operationId = "listRestoreTasks", summary = "查询恢复任务列表",
      description = "分页查询恢复任务历史记录")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "恢复任务列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<RestoreTaskListVo>> list(
      @Valid @ParameterObject RestoreFindDto dto) {
    return ApiLocaleResult.success(restoreTaskFacade.list(dto));
  }

  @Operation(operationId = "validateBackupFile", summary = "验证备份文件",
      description = "验证备份文件的完整性和有效性，恢复前的预检查")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "备份文件验证成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/validate")
  public ApiLocaleResult<BackupValidationVo> validate(
      @Valid @RequestBody BackupValidateDto dto) {
    return ApiLocaleResult.success(restoreTaskFacade.validate(dto));
  }
}
