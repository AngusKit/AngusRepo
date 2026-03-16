package cloud.xcan.angus.core.repo.interfaces.format.protocol;

import cloud.xcan.angus.core.repo.application.query.repository.RepositoryQuery;
import cloud.xcan.angus.core.repo.domain.format.ArtifactFormatHandler;
import cloud.xcan.angus.core.repo.domain.format.FormatHandlerRegistry;
import cloud.xcan.angus.core.repo.domain.format.ValidationResult;
import cloud.xcan.angus.core.repo.domain.format.store.BlobStore;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Helm chart repository protocol controller.
 * Implements the Helm Chart Repository API for helm client compatibility.
 * Supports hosted/proxy/group repository types with chart name and version based paths.
 *
 * <p>Path format: /helm/{repositoryName}/charts/mychart-1.0.0.tgz
 */
@Tag(name = "Helm Protocol", description = "Helm Chart仓库协议 - 支持Helm客户端的Chart上传、下载、删除和索引查询")
@Validated
@RestController
@RequestMapping("/helm")
public class HelmProtocolRest {

  private static final MediaType APPLICATION_X_YAML = MediaType.parseMediaType("application/x-yaml");
  private static final MediaType APPLICATION_GZIP = MediaType.parseMediaType("application/gzip");

  @Resource
  private FormatHandlerRegistry formatHandlerRegistry;

  @Resource
  private RepositoryQuery repositoryQuery;

  @Resource
  private BlobStore blobStore;

  @Operation(summary = "获取Chart仓库索引", description = "获取Helm Chart仓库的index.yaml索引文件",
      operationId = "helm:index")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取索引成功"),
      @ApiResponse(responseCode = "404", description = "仓库不存在")
  })
  @GetMapping("/{repositoryName}/index.yaml")
  public ResponseEntity<?> index(@Parameter(name = "repositoryName", description = "仓库名称") @PathVariable String repositoryName) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.HELM);

    ArtifactFormatHandler handler = formatHandlerRegistry.getHandler(RepositoryFormat.HELM);
    byte[] index = handler.generateIndex(repository);
    return ResponseEntity.ok()
        .contentType(APPLICATION_X_YAML)
        .contentLength(index.length)
        .body(index);
  }

  @Operation(summary = "下载Chart包", description = "下载Helm Chart压缩包(.tgz文件)",
      operationId = "helm:downloadChart")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "下载成功"),
      @ApiResponse(responseCode = "404", description = "Chart不存在")
  })
  @GetMapping("/{repositoryName}/charts/{filename}")
  public ResponseEntity<?> downloadChart(@Parameter(name = "repositoryName", description = "仓库名称") @PathVariable String repositoryName,
      @Parameter(name = "filename", description = "文件名") @PathVariable String filename) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.HELM);

    String artifactPath = "charts/" + filename;
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    if (!blobStore.exists(tenantId, repoId, artifactPath)) {
      return ResponseEntity.notFound().build();
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, artifactPath);
    long size = blobStore.size(tenantId, repoId, artifactPath);
    return ResponseEntity.ok()
        .contentType(APPLICATION_GZIP)
        .contentLength(size)
        .body(new org.springframework.core.io.InputStreamResource(data));
  }

  @Operation(summary = "上传Chart包", description = "上传Helm Chart到hosted类型仓库",
      operationId = "helm:uploadChart")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "上传成功"),
      @ApiResponse(responseCode = "400", description = "请求无效"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持上传")
  })
  @PostMapping(value = "/{repositoryName}/api/charts",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> uploadChart(@Parameter(name = "repositoryName", description = "仓库名称") @PathVariable String repositoryName,
      @RequestParam("chart") MultipartFile chart) throws IOException {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.HELM);
    validateHosted(repository);

    String fileName = chart.getOriginalFilename();
    if (fileName == null || fileName.isBlank()) {
      return ResponseEntity.badRequest()
          .body("{\"error\":\"Chart file name is required\"}");
    }

    ArtifactFormatHandler handler = formatHandlerRegistry.getHandler(RepositoryFormat.HELM);
    ValidationResult validation = handler.validateArtifact(null, fileName);
    if (!validation.isValid()) {
      return ResponseEntity.badRequest()
          .body("{\"error\":\"" + String.join(", ", validation.getErrors()) + "\"}");
    }

    String artifactPath = "charts/" + fileName;
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    try (InputStream inputStream = chart.getInputStream()) {
      blobStore.store(tenantId, repoId, artifactPath, inputStream);
    }

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(summary = "删除Chart包", description = "从hosted类型仓库删除指定版本的Chart",
      operationId = "helm:deleteChart")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持删除"),
      @ApiResponse(responseCode = "404", description = "Chart不存在")
  })
  @DeleteMapping("/{repositoryName}/api/charts/{name}/{version}")
  public ResponseEntity<?> deleteChart(@Parameter(name = "repositoryName", description = "仓库名称") @PathVariable String repositoryName,
      @Parameter(name = "name", description = "镜像名称") @PathVariable String name, @Parameter(name = "version", description = "版本号") @PathVariable String version) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.HELM);
    validateHosted(repository);

    String artifactPath = "charts/" + name + "-" + version + ".tgz";
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    if (!blobStore.exists(tenantId, repoId, artifactPath)) {
      return ResponseEntity.notFound().build();
    }
    blobStore.delete(tenantId, repoId, artifactPath);
    return ResponseEntity.noContent().build();
  }

  // ===== Helper methods =====

  private void validateFormat(RepoEntity repository, RepositoryFormat expectedFormat) {
    if (repository.getFormat() != expectedFormat) {
      throw new IllegalArgumentException(
          "Repository '" + repository.getName() + "' is not a " + expectedFormat.getValue()
              + " repository");
    }
  }

  private void validateHosted(RepoEntity repository) {
    if (repository.getType() != RepositoryType.HOSTED) {
      throw new IllegalStateException(
          "Upload/delete operations are only allowed on hosted repositories");
    }
  }
}
