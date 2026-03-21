package cloud.xcan.angus.core.gm.interfaces.log;

import static cloud.xcan.angus.core.gm.infra.utils.DownloadResponseUtils.buildDownloadResourceResponseEntity;

import cloud.xcan.angus.core.gm.interfaces.log.facade.SystemLogFacade;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.SystemLogBatchDeleteDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.SystemLogContentDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.SystemLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.SystemLogStatisticsDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.SystemLogContentVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.SystemLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.SystemLogStatisticsVo;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SystemLog", description = "系统日志管理 - 系统日志文件的查询、查看、下载、删除、统计等功能")
@Validated
@RestController
@RequestMapping("/api/v1/logs/system")
public class SystemLogRest {

  @Resource
  private SystemLogFacade systemLogFacade;

  @Operation(operationId = "getSystemLogDetail", summary = "获取系统日志详情",
      description = "获取指定ID的系统日志文件详情信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "日志详情获取成功"),
      @ApiResponse(responseCode = "404", description = "日志不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<SystemLogDetailVo> getDetail(
      @Parameter(description = "日志文件ID") @PathVariable Long id) {
    return ApiLocaleResult.success(systemLogFacade.getDetail(id));
  }

  @Operation(operationId = "getSystemLogList", summary = "获取系统日志列表",
      description = "查询系统日志文件列表，支持筛选和排序")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "日志列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<SystemLogDetailVo>> list(
      @Valid @ParameterObject SystemLogFindDto dto) {
    return ApiLocaleResult.success(systemLogFacade.list(dto));
  }

  @Operation(operationId = "getSystemLogContent", summary = "查看日志内容",
      description = """
          获取日志文件内容，支持分页、搜索和tail模式（类似tail -n）。
          - 普通模式：使用page和size参数进行分页查询
          - tail模式：设置tail=true，从文件末尾读取指定行数（tailLines参数，默认使用size值）
          - 行号范围：使用startLine和endLine参数指定行号范围
          示例：tail=true&tailLines=1000 表示读取文件最后1000行""")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "日志内容获取成功"),
      @ApiResponse(responseCode = "404", description = "日志不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/content")
  public ApiLocaleResult<SystemLogContentVo> getContent(
      @Parameter(description = "日志文件ID") @PathVariable Long id,
      @Valid @ParameterObject SystemLogContentDto dto) {
    return ApiLocaleResult.success(systemLogFacade.getContent(id, dto));
  }

  @Operation(operationId = "downloadSystemLog", summary = "下载日志文件",
      description = "下载指定的日志文件")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "下载成功"),
      @ApiResponse(responseCode = "404", description = "日志不存在")
  })
  @GetMapping("/{id}/download")
  public ResponseEntity<org.springframework.core.io.Resource> download(
      @Parameter(description = "日志文件ID") @PathVariable Long id) throws IOException {
    var result = systemLogFacade.download(id);
    return buildDownloadResourceResponseEntity(-1, result.mediaType(), result.filename(),
        result.filesize(), result.resource());
  }

  @Operation(operationId = "deleteSystemLog", summary = "删除日志文件",
      description = "删除指定的日志文件（物理删除或归档）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功"),
      @ApiResponse(responseCode = "404", description = "日志不存在")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(description = "日志文件ID") @PathVariable Long id,
      @Parameter(description = "是否永久删除，默认false（归档）") @RequestParam(required = false,
          defaultValue = "false") Boolean permanent) {
    systemLogFacade.delete(id, permanent);
  }

  @Operation(operationId = "batchDeleteSystemLogs", summary = "批量删除日志文件",
      description = "批量删除日志文件")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/batch")
  public void batchDelete(@Valid @RequestBody SystemLogBatchDeleteDto dto) {
    systemLogFacade.batchDelete(dto);
  }

  @Operation(operationId = "getSystemLogStatistics", summary = "获取系统日志统计数据",
      description = "获取系统日志文件统计信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "统计数据获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<SystemLogStatisticsVo> getStatistics(
      @Valid @ParameterObject SystemLogStatisticsDto dto) {
    return ApiLocaleResult.success(systemLogFacade.getStatistics(dto));
  }
}
