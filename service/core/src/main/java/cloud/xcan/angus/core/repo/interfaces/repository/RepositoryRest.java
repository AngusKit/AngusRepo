package cloud.xcan.angus.core.repo.interfaces.repository;

import cloud.xcan.angus.core.repo.interfaces.repository.facade.RepositoryFacade;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.dto.RepositoryBatchDeleteDto;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.dto.RepositoryCreateDto;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.dto.RepositoryFindDto;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.dto.RepositoryStatusUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.dto.RepositoryUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.vo.RepositoryDetailVo;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.vo.RepositoryStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.vo.RepositoryUrlVo;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Repositories", description = "仓库管理 - 仓库的创建、更新、删除、查询、统计")
@Validated
@RestController
@RequestMapping("/api/v1/repositories")
public class RepositoryRest {

  @Resource
  private RepositoryFacade repositoryFacade;

  @Operation(summary = "创建仓库", description = "创建新的制品仓库",
      operationId = "repository:create")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "仓库创建成功")
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiLocaleResult<RepositoryDetailVo> create(
      @Valid @RequestBody RepositoryCreateDto dto) {
    return ApiLocaleResult.success(repositoryFacade.create(dto));
  }

  @Operation(summary = "更新仓库", description = "更新仓库基本信息",
      operationId = "repository:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @PutMapping("/{id}")
  public ApiLocaleResult<RepositoryDetailVo> update(
      @Parameter(name = "id", description = "仓库ID") @PathVariable Long id, @Valid @RequestBody RepositoryUpdateDto dto) {
    return ApiLocaleResult.success(repositoryFacade.update(id, dto));
  }

  @Operation(summary = "修改仓库状态", description = "修改仓库上线/下线状态",
      operationId = "repository:updateStatus")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "状态更新成功")
  })
  @PutMapping("/{id}/status")
  public ApiLocaleResult<RepositoryDetailVo> updateStatus(
      @Parameter(name = "id", description = "仓库ID") @PathVariable Long id, @Valid @RequestBody RepositoryStatusUpdateDto dto) {
    return ApiLocaleResult.success(repositoryFacade.updateStatus(id, dto));
  }

  @Operation(summary = "删除仓库", description = "删除指定仓库",
      operationId = "repository:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@Parameter(name = "id", description = "仓库ID") @PathVariable Long id) {
    repositoryFacade.delete(id);
  }

  @Operation(summary = "查询仓库详情", description = "获取仓库详细信息",
      operationId = "repository:getById")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功"),
      @ApiResponse(responseCode = "404", description = "仓库不存在")
  })
  @GetMapping("/{id}")
  public ApiLocaleResult<RepositoryDetailVo> getById(@Parameter(name = "id", description = "仓库ID") @PathVariable Long id) {
    return ApiLocaleResult.success(repositoryFacade.getById(id));
  }

  @Operation(summary = "查询仓库列表", description = "分页查询仓库列表，支持多维度筛选",
      operationId = "repository:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping
  public ApiLocaleResult<PageResult<RepositoryDetailVo>> list(
      @Valid @ParameterObject RepositoryFindDto dto) {
    return ApiLocaleResult.success(repositoryFacade.list(dto));
  }

  @Operation(summary = "查询仓库统计", description = "获取仓库统计数据",
      operationId = "repository:getStatistics")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/statistics")
  public ApiLocaleResult<RepositoryStatisticsVo> getStatistics() {
    return ApiLocaleResult.success(repositoryFacade.getStatistics());
  }

  @Operation(summary = "批量删除仓库", description = "批量删除仓库",
      operationId = "repository:deleteBatch")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "批量删除成功")
  })
  @DeleteMapping("/batch")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBatch(@Valid @RequestBody RepositoryBatchDeleteDto dto) {
    repositoryFacade.deleteBatch(dto);
  }

  @Operation(summary = "获取仓库URL", description = "获取仓库的访问URL",
      operationId = "repository:getUrl")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/{id}/url")
  public ApiLocaleResult<RepositoryUrlVo> getUrl(@Parameter(name = "id", description = "仓库ID") @PathVariable Long id) {
    return ApiLocaleResult.success(repositoryFacade.getUrl(id));
  }
}
