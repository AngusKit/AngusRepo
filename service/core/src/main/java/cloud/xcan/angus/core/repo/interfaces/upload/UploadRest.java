package cloud.xcan.angus.core.repo.interfaces.upload;

import cloud.xcan.angus.core.repo.interfaces.upload.facade.UploadFacade;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.dto.BatchUploadCreateDto;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.dto.UploadCompleteDto;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.dto.UploadTaskCreateDto;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.dto.UploadTaskFindDto;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.vo.UploadStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.vo.UploadTaskVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Uploads", description = "制品上传 - 上传任务的创建、文件上传、完成、取消、查询、统计")
@Validated
@RestController
@RequestMapping("/api/v1/uploads")
public class UploadRest {

  @Resource
  private UploadFacade uploadFacade;

  @Operation(summary = "创建上传任务", description = "创建新的制品上传任务",
      operationId = "upload:createTask")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "上传任务创建成功")
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiLocaleResult<UploadTaskVo> createTask(
      @Valid @RequestBody UploadTaskCreateDto dto) {
    return ApiLocaleResult.success(uploadFacade.createTask(dto));
  }

  @Operation(summary = "上传文件", description = "上传文件到指定任务",
      operationId = "upload:uploadFile")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "文件上传成功")
  })
  @PostMapping(value = "/{taskId}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiLocaleResult<?> uploadFile(
      @PathVariable Long taskId,
      @RequestParam("file") MultipartFile file) {
    uploadFacade.uploadFile(taskId, file);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "完成上传任务", description = "标记上传任务为完成状态",
      operationId = "upload:completeTask")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "上传任务完成")
  })
  @PutMapping("/{taskId}/complete")
  public ApiLocaleResult<UploadTaskVo> completeTask(
      @PathVariable Long taskId,
      @Valid @RequestBody UploadCompleteDto dto) {
    return ApiLocaleResult.success(uploadFacade.completeTask(taskId, dto));
  }

  @Operation(summary = "取消上传任务", description = "取消指定的上传任务",
      operationId = "upload:cancelTask")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "上传任务已取消")
  })
  @PutMapping("/{taskId}/cancel")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void cancelTask(@PathVariable Long taskId) {
    uploadFacade.cancelTask(taskId);
  }

  @Operation(summary = "查询上传任务详情", description = "获取指定上传任务的详细信息",
      operationId = "upload:getTask")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功"),
      @ApiResponse(responseCode = "404", description = "上传任务不存在")
  })
  @GetMapping("/{taskId}")
  public ApiLocaleResult<UploadTaskVo> getTask(@PathVariable Long taskId) {
    return ApiLocaleResult.success(uploadFacade.getTask(taskId));
  }

  @Operation(summary = "查询上传任务列表", description = "分页查询上传任务列表，支持多维度筛选",
      operationId = "upload:listTasks")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping
  public ApiLocaleResult<PageResult<UploadTaskVo>> listTasks(
      @Valid @ParameterObject UploadTaskFindDto dto) {
    return ApiLocaleResult.success(uploadFacade.listTasks(dto));
  }

  @Operation(summary = "批量创建上传任务", description = "批量创建多个上传任务",
      operationId = "upload:createBatchTasks")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "批量创建成功")
  })
  @PostMapping("/batch")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiLocaleResult<List<UploadTaskVo>> createBatchTasks(
      @Valid @RequestBody BatchUploadCreateDto dto) {
    return ApiLocaleResult.success(uploadFacade.createBatchTasks(dto));
  }

  @Operation(summary = "查询上传统计", description = "获取上传任务统计数据",
      operationId = "upload:getStatistics")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/statistics")
  public ApiLocaleResult<UploadStatisticsVo> getStatistics() {
    return ApiLocaleResult.success(uploadFacade.getStatistics());
  }
}
