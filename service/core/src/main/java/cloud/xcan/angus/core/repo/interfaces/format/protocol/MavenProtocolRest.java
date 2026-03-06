package cloud.xcan.angus.core.repo.interfaces.format.protocol;

import cloud.xcan.angus.core.repo.application.cmd.artifact.ArtifactCmd;
import cloud.xcan.angus.core.repo.application.query.repository.RepositoryQuery;
import cloud.xcan.angus.core.repo.domain.artifact.ArtifactMetadata;
import cloud.xcan.angus.core.repo.domain.format.ArtifactFormatHandler;
import cloud.xcan.angus.core.repo.domain.format.FormatHandlerRegistry;
import cloud.xcan.angus.core.repo.domain.format.ValidationResult;
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
import jakarta.servlet.http.HttpServletResponse;
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
 * Maven repository protocol controller.
 * Implements standard Maven repository HTTP protocol for mvn/gradle client compatibility.
 * Supports hosted/proxy/group repository types with GAV coordinate-based paths.
 *
 * <p>Path format: /maven/{repositoryName}/org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar
 */
@Tag(name = "Maven Protocol", description = "Maven仓库协议 - 支持Maven客户端的制品上传、下载、删除和校验")
@Validated
@RestController
@RequestMapping("/maven")
public class MavenProtocolRest {

  @Resource
  private FormatHandlerRegistry formatHandlerRegistry;

  @Resource
  private RepositoryQuery repositoryQuery;

  @Resource(name = "localBlobStore")
  private BlobStore blobStore;

  @Operation(summary = "下载Maven制品", description = "下载Maven制品文件，支持.jar/.pom/.war/.ear/.xml/.sha1/.md5等",
      operationId = "maven:download")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "下载成功"),
      @ApiResponse(responseCode = "404", description = "制品不存在")
  })
  @GetMapping("/{repositoryName}/**")
  public ResponseEntity<?> download(@PathVariable String repositoryName,
      HttpServletRequest request) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.MAVEN);
    String artifactPath = extractPath(request, repositoryName);

    // Handle maven-metadata.xml requests
    if (artifactPath.endsWith("maven-metadata.xml")) {
      ArtifactFormatHandler handler = formatHandlerRegistry.getHandler(RepositoryFormat.MAVEN);
      byte[] index = handler.generateIndex(repository);
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_XML)
          .contentLength(index.length)
          .body(index);
    }

    // Handle checksum files
    if (isChecksumFile(artifactPath)) {
      return handleChecksumRequest(repository, artifactPath);
    }

    // Handle regular artifact download
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

  @Operation(summary = "上传Maven制品", description = "上传Maven制品到hosted类型仓库",
      operationId = "maven:upload")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "上传成功"),
      @ApiResponse(responseCode = "400", description = "请求无效"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持上传")
  })
  @PutMapping("/{repositoryName}/**")
  public ResponseEntity<?> upload(@PathVariable String repositoryName,
      HttpServletRequest request) throws IOException {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.MAVEN);
    validateHosted(repository);
    String artifactPath = extractPath(request, repositoryName);

    // Validate artifact format
    ArtifactFormatHandler handler = formatHandlerRegistry.getHandler(RepositoryFormat.MAVEN);
    String fileName = artifactPath.substring(artifactPath.lastIndexOf('/') + 1);
    ValidationResult validation = handler.validateArtifact(null, fileName);
    if (!validation.isValid()) {
      return ResponseEntity.badRequest()
          .body("{\"error\":\"" + String.join(", ", validation.getErrors()) + "\"}");
    }

    // Store artifact
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    try (InputStream inputStream = request.getInputStream()) {
      blobStore.store(tenantId, repoId, artifactPath, inputStream);
    }

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(summary = "检查Maven制品是否存在", description = "检查指定路径的Maven制品是否存在",
      operationId = "maven:exists")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "制品存在"),
      @ApiResponse(responseCode = "404", description = "制品不存在")
  })
  @RequestMapping(value = "/{repositoryName}/**", method = RequestMethod.HEAD)
  public ResponseEntity<?> exists(@PathVariable String repositoryName,
      HttpServletRequest request) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.MAVEN);
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

  @Operation(summary = "删除Maven制品", description = "从hosted类型仓库删除Maven制品",
      operationId = "maven:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持删除"),
      @ApiResponse(responseCode = "404", description = "制品不存在")
  })
  @DeleteMapping("/{repositoryName}/**")
  public ResponseEntity<?> delete(@PathVariable String repositoryName,
      HttpServletRequest request) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.MAVEN);
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

  // ===== Helper methods =====

  private String extractPath(HttpServletRequest request, String repositoryName) {
    String uri = request.getRequestURI();
    String prefix = "/maven/" + repositoryName + "/";
    int idx = uri.indexOf(prefix);
    if (idx >= 0) {
      return uri.substring(idx + prefix.length());
    }
    return uri;
  }

  private void validateFormat(RepoEntity repository, RepositoryFormat expectedFormat) {
    if (repository.getFormat() != expectedFormat) {
      throw new IllegalArgumentException(
          "Repository '" + repository.getName() + "' is not a " + expectedFormat.getValue() + " repository");
    }
  }

  private void validateHosted(RepoEntity repository) {
    if (repository.getType() != RepositoryType.HOSTED) {
      throw new IllegalStateException(
          "Upload/delete operations are only allowed on hosted repositories");
    }
  }

  private boolean isChecksumFile(String path) {
    return path.endsWith(".sha1") || path.endsWith(".md5")
        || path.endsWith(".sha256") || path.endsWith(".sha512");
  }

  private ResponseEntity<?> handleChecksumRequest(RepoEntity repository, String checksumPath) {
    // Return checksum for the base artifact
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    if (blobStore.exists(tenantId, repoId, checksumPath)) {
      InputStream data = blobStore.retrieve(tenantId, repoId, checksumPath);
      return ResponseEntity.ok()
          .contentType(MediaType.TEXT_PLAIN)
          .body(new org.springframework.core.io.InputStreamResource(data));
    }
    return ResponseEntity.notFound().build();
  }

  private MediaType resolveContentType(String path) {
    if (path.endsWith(".pom") || path.endsWith(".xml")) {
      return MediaType.APPLICATION_XML;
    } else if (path.endsWith(".jar") || path.endsWith(".war") || path.endsWith(".ear")) {
      return MediaType.APPLICATION_OCTET_STREAM;
    } else if (path.endsWith(".sha1") || path.endsWith(".md5")
        || path.endsWith(".sha256") || path.endsWith(".sha512")) {
      return MediaType.TEXT_PLAIN;
    }
    return MediaType.APPLICATION_OCTET_STREAM;
  }
}
