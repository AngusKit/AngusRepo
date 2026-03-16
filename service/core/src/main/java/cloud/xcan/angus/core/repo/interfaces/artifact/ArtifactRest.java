package cloud.xcan.angus.core.repo.interfaces.artifact;

import cloud.xcan.angus.core.repo.interfaces.artifact.facade.ArtifactFacade;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.dto.ArtifactBatchDeleteDto;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.dto.ArtifactCreateDto;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.dto.ArtifactFindDto;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.dto.ArtifactUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.vo.ArtifactDetailVo;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.vo.ArtifactStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.vo.ArtifactVersionVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
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

@Tag(name = "Artifacts", description = "制品管理 - 制品的创建、更新、删除、查询、下载、收藏、版本管理")
@Validated
@RestController
@RequestMapping("/api/v1/artifacts")
public class ArtifactRest {

  @Resource
  private ArtifactFacade artifactFacade;

  @Operation(summary = "创建制品", description = "创建新的制品",
      operationId = "artifact:create")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "制品创建成功")
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiLocaleResult<ArtifactDetailVo> create(
      @Valid @RequestBody ArtifactCreateDto dto) {
    return ApiLocaleResult.success(artifactFacade.create(dto));
  }

  @Operation(summary = "更新制品", description = "更新制品基本信息",
      operationId = "artifact:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @PutMapping("/{id}")
  public ApiLocaleResult<ArtifactDetailVo> update(
      @Parameter(name = "id", description = "id") @PathVariable Long id, @Valid @RequestBody ArtifactUpdateDto dto) {
    return ApiLocaleResult.success(artifactFacade.update(id, dto));
  }

  @Operation(summary = "标记为最新版本", description = "将指定制品标记为最新版本",
      operationId = "artifact:markLatest")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "标记成功")
  })
  @PutMapping("/{id}/latest")
  public ApiLocaleResult<?> markLatest(@Parameter(name = "id", description = "id") @PathVariable Long id) {
    artifactFacade.markLatest(id);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "删除制品", description = "删除指定制品",
      operationId = "artifact:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@Parameter(name = "id", description = "id") @PathVariable Long id) {
    artifactFacade.delete(id);
  }

  @Operation(summary = "查询制品详情", description = "获取制品详细信息",
      operationId = "artifact:getById")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功"),
      @ApiResponse(responseCode = "404", description = "制品不存在")
  })
  @GetMapping("/{id}")
  public ApiLocaleResult<ArtifactDetailVo> getById(@Parameter(name = "id", description = "id") @PathVariable Long id) {
    return ApiLocaleResult.success(artifactFacade.getById(id));
  }

  @Operation(summary = "查询制品列表", description = "分页查询制品列表，支持多维度筛选",
      operationId = "artifact:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping
  public ApiLocaleResult<PageResult<ArtifactDetailVo>> list(
      @Valid @ParameterObject ArtifactFindDto dto) {
    return ApiLocaleResult.success(artifactFacade.list(dto));
  }

  @Operation(summary = "查询制品统计", description = "获取制品统计数据",
      operationId = "artifact:getStatistics")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/statistics")
  public ApiLocaleResult<ArtifactStatisticsVo> getStatistics() {
    return ApiLocaleResult.success(artifactFacade.getStatistics());
  }

  @Operation(summary = "下载制品", description = "下载制品文件",
      operationId = "artifact:download")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "下载成功"),
      @ApiResponse(responseCode = "404", description = "制品不存在")
  })
  @GetMapping("/{id}/download")
  public void download(@Parameter(name = "id", description = "id") @PathVariable Long id, HttpServletResponse response) {
    artifactFacade.download(id, response);
  }

  @Operation(summary = "获取下载链接", description = "获取制品的下载URL",
      operationId = "artifact:getDownloadUrl")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/{id}/download-url")
  public ApiLocaleResult<String> getDownloadUrl(@Parameter(name = "id", description = "id") @PathVariable Long id) {
    return ApiLocaleResult.success(artifactFacade.getDownloadUrl(id));
  }

  @Operation(summary = "收藏制品", description = "收藏指定制品",
      operationId = "artifact:addStar")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "收藏成功")
  })
  @PutMapping("/{id}/star")
  public ApiLocaleResult<?> addStar(@Parameter(name = "id", description = "id") @PathVariable Long id) {
    Long userId = PrincipalContext.getUserId();
    artifactFacade.addStar(id, userId);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "取消收藏", description = "取消收藏指定制品",
      operationId = "artifact:removeStar")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "取消收藏成功")
  })
  @DeleteMapping("/{id}/star")
  public ApiLocaleResult<?> removeStar(@Parameter(name = "id", description = "id") @PathVariable Long id) {
    Long userId = PrincipalContext.getUserId();
    artifactFacade.removeStar(id, userId);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "查询制品版本列表", description = "获取制品的所有版本",
      operationId = "artifact:getVersions")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/{id}/versions")
  public ApiLocaleResult<List<ArtifactVersionVo>> getVersions(@Parameter(name = "id", description = "id") @PathVariable Long id) {
    return ApiLocaleResult.success(artifactFacade.getVersions(id));
  }

  @Operation(summary = "批量删除制品", description = "批量删除制品",
      operationId = "artifact:deleteBatch")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "批量删除成功")
  })
  @DeleteMapping("/batch")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBatch(@Valid @RequestBody ArtifactBatchDeleteDto dto) {
    artifactFacade.deleteBatch(dto);
  }
}
