package cloud.xcan.angus.core.gm.interfaces.backup;

import static cloud.xcan.angus.core.gm.infra.utils.DownloadResponseUtils.buildDownloadResourceResponseEntity;

import cloud.xcan.angus.core.gm.interfaces.backup.facade.BackupFacade;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupFindDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupListVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupStatsVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.io.IOException;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Backup", description = "备份管理 - 系统备份、恢复管理")
@Validated
@RestController
@RequestMapping("/api/v1/backup")
public class BackupRest {

  @Resource
  private BackupFacade backupFacade;

  @Operation(operationId = "createBackup", summary = "创建备份", description = "创建一个新的备份任务，指定备份名称、类型和描述等信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "备份任务创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/records")
  public ApiLocaleResult<BackupDetailVo> createBackup(
      @Valid @RequestBody BackupCreateDto dto) {
    return ApiLocaleResult.success(backupFacade.createBackup(dto));
  }

  @Operation(operationId = "deleteBackup", summary = "删除备份", description = "根据备份ID删除指定的备份记录")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "备份删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/records/{id}")
  public void deleteBackup(
      @Parameter(description = "备份ID") @PathVariable Long id) {
    backupFacade.deleteBackup(id);
  }

  @Operation(operationId = "runBackup", summary = "重新运行备份", description = "对失败的备份任务重新运行，将状态重置为待执行，由调度任务执行")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "已加入执行队列")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/records/{id}/run")
  public ApiLocaleResult<?> runBackup(
      @Parameter(description = "备份ID") @PathVariable Long id) {
    backupFacade.runBackup(id);
    return ApiLocaleResult.success();
  }

  @Operation(operationId = "getBackupDetail", summary = "获取备份详情", description = "根据备份ID获取指定备份记录的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "备份详情获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/records/{id}")
  public ApiLocaleResult<BackupDetailVo> getBackupDetail(
      @Parameter(description = "备份ID") @PathVariable Long id) {
    return ApiLocaleResult.success(backupFacade.getBackupDetail(id));
  }

  @Operation(operationId = "getBackupRecords", summary = "获取备份记录列表", description = "分页获取备份记录列表，支持按类型、状态等条件筛选")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "备份记录列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/records")
  public ApiLocaleResult<PageResult<BackupListVo>> listRecords(
      @Valid @ParameterObject BackupFindDto dto) {
    return ApiLocaleResult.success(backupFacade.listRecords(dto));
  }

  @Operation(operationId = "downloadBackup", summary = "下载备份文件", description = "根据备份ID下载对应的备份文件")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "备份文件下载成功"),
      @ApiResponse(responseCode = "404", description = "备份不存在")
  })
  @GetMapping("/records/{id}/download")
  public ResponseEntity<org.springframework.core.io.Resource> downloadBackup(
      @Parameter(description = "备份ID") @PathVariable Long id) throws IOException {
    var result = backupFacade.downloadBackup(id);
    return buildDownloadResourceResponseEntity(-1, result.mediaType(), result.filename(),
        result.filesize(), result.resource());
  }

  @Operation(operationId = "getBackupStats", summary = "获取备份统计数据", description = "获取系统中所有备份记录的统计信息，包括总数、成功数、失败数和总大小")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "备份统计数据获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<BackupStatsVo> getStats() {
    return ApiLocaleResult.success(backupFacade.getStats());
  }

}
