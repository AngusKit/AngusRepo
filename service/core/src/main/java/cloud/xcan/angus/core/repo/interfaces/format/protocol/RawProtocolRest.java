package cloud.xcan.angus.core.repo.interfaces.format.protocol;

import cloud.xcan.angus.core.repo.application.query.repository.RepositoryQuery;
import cloud.xcan.angus.core.repo.domain.format.FormatHandlerRegistry;
import cloud.xcan.angus.core.repo.domain.format.store.BlobStore;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Raw file storage protocol controller.
 * Provides generic file storage and retrieval for any file type.
 * Supports hosted/proxy/group repository types with arbitrary path-based access.
 *
 * <p>Path format: /raw/{repositoryName}/path/to/file.ext
 */
@Tag(name = "Raw Protocol", description = "Raw文件存储协议 - 支持任意文件的上传、下载、删除和存在性检查")
@Validated
@RestController
@RequestMapping("/raw")
public class RawProtocolRest {

  @Resource
  private FormatHandlerRegistry formatHandlerRegistry;

  @Resource
  private RepositoryQuery repositoryQuery;

  @Resource
  private BlobStore blobStore;

  @Operation(summary = "下载文件", description = "从Raw仓库下载指定路径的文件",
      operationId = "raw:download")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "下载成功"),
      @ApiResponse(responseCode = "404", description = "文件不存在")
  })
  @GetMapping("/{repositoryName}/**")
  public ResponseEntity<?> download(@PathVariable String repositoryName,
      HttpServletRequest request) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.RAW);
    String artifactPath = extractPath(request, repositoryName);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    if (!blobStore.exists(tenantId, repoId, artifactPath)) {
      return ResponseEntity.notFound().build();
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, artifactPath);
    long size = blobStore.size(tenantId, repoId, artifactPath);
    return ResponseEntity.ok()
        .contentType(resolveContentType(artifactPath))
        .contentLength(size)
        .body(new org.springframework.core.io.InputStreamResource(data));
  }

  @Operation(summary = "上传文件", description = "上传文件到hosted类型Raw仓库",
      operationId = "raw:upload")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "上传成功"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持上传")
  })
  @PutMapping("/{repositoryName}/**")
  public ResponseEntity<?> upload(@PathVariable String repositoryName,
      HttpServletRequest request) throws IOException {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.RAW);
    validateHosted(repository);
    String artifactPath = extractPath(request, repositoryName);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    try (InputStream inputStream = request.getInputStream()) {
      blobStore.store(tenantId, repoId, artifactPath, inputStream);
    }

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(summary = "删除文件", description = "从hosted类型Raw仓库删除指定路径的文件",
      operationId = "raw:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持删除"),
      @ApiResponse(responseCode = "404", description = "文件不存在")
  })
  @DeleteMapping("/{repositoryName}/**")
  public ResponseEntity<?> delete(@PathVariable String repositoryName,
      HttpServletRequest request) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.RAW);
    validateHosted(repository);
    String artifactPath = extractPath(request, repositoryName);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    if (!blobStore.exists(tenantId, repoId, artifactPath)) {
      return ResponseEntity.notFound().build();
    }
    blobStore.delete(tenantId, repoId, artifactPath);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "检查文件是否存在", description = "检查Raw仓库中指定路径的文件是否存在",
      operationId = "raw:exists")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "文件存在"),
      @ApiResponse(responseCode = "404", description = "文件不存在")
  })
  @RequestMapping(value = "/{repositoryName}/**", method = RequestMethod.HEAD)
  public ResponseEntity<?> exists(@PathVariable String repositoryName,
      HttpServletRequest request) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.RAW);
    String artifactPath = extractPath(request, repositoryName);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    if (blobStore.exists(tenantId, repoId, artifactPath)) {
      long size = blobStore.size(tenantId, repoId, artifactPath);
      return ResponseEntity.ok()
          .contentLength(size)
          .contentType(resolveContentType(artifactPath))
          .build();
    }
    return ResponseEntity.notFound().build();
  }

  // ===== Helper methods =====

  private String extractPath(HttpServletRequest request, String repositoryName) {
    String uri = request.getRequestURI();
    String prefix = "/raw/" + repositoryName + "/";
    int idx = uri.indexOf(prefix);
    if (idx >= 0) {
      return uri.substring(idx + prefix.length());
    }
    return uri;
  }

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

  private MediaType resolveContentType(String path) {
    if (path.endsWith(".html") || path.endsWith(".htm")) {
      return MediaType.TEXT_HTML;
    } else if (path.endsWith(".json")) {
      return MediaType.APPLICATION_JSON;
    } else if (path.endsWith(".xml")) {
      return MediaType.APPLICATION_XML;
    } else if (path.endsWith(".txt") || path.endsWith(".log") || path.endsWith(".md")) {
      return MediaType.TEXT_PLAIN;
    } else if (path.endsWith(".css")) {
      return MediaType.parseMediaType("text/css");
    } else if (path.endsWith(".js")) {
      return MediaType.parseMediaType("application/javascript");
    } else if (path.endsWith(".png")) {
      return MediaType.IMAGE_PNG;
    } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
      return MediaType.IMAGE_JPEG;
    } else if (path.endsWith(".gif")) {
      return MediaType.IMAGE_GIF;
    } else if (path.endsWith(".svg")) {
      return MediaType.parseMediaType("image/svg+xml");
    } else if (path.endsWith(".pdf")) {
      return MediaType.APPLICATION_PDF;
    } else if (path.endsWith(".zip")) {
      return MediaType.parseMediaType("application/zip");
    } else if (path.endsWith(".gz") || path.endsWith(".tgz")) {
      return MediaType.parseMediaType("application/gzip");
    } else if (path.endsWith(".tar")) {
      return MediaType.parseMediaType("application/x-tar");
    } else if (path.endsWith(".yaml") || path.endsWith(".yml")) {
      return MediaType.parseMediaType("application/x-yaml");
    }
    return MediaType.APPLICATION_OCTET_STREAM;
  }
}
