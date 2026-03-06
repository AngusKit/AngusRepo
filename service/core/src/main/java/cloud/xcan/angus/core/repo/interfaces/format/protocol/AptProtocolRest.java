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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * APT (Debian) repository protocol controller.
 * Implements the Debian APT repository protocol for apt client compatibility.
 * Supports hosted/proxy/group repository types with distribution and component based paths.
 *
 * <p>Path format: /apt/{repositoryName}/dists/{distribution}/{component}/binary-{arch}/Packages
 */
@Tag(name = "APT Protocol", description = "APT(Debian)仓库协议 - 支持apt客户端的软件包上传、下载和索引查询")
@Validated
@RestController
@RequestMapping("/apt")
public class AptProtocolRest {

  private static final MediaType APPLICATION_GZIP = MediaType.parseMediaType("application/gzip");
  private static final MediaType APPLICATION_DEB =
      MediaType.parseMediaType("application/vnd.debian.binary-package");

  @Resource
  private FormatHandlerRegistry formatHandlerRegistry;

  @Resource
  private RepositoryQuery repositoryQuery;

  @Resource
  private BlobStore blobStore;

  @Operation(summary = "获取Release文件", description = "获取APT仓库指定发行版的Release元数据文件",
      operationId = "apt:release")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "发行版不存在")
  })
  @GetMapping("/{repositoryName}/dists/{distribution}/Release")
  public ResponseEntity<?> release(@PathVariable String repositoryName,
      @PathVariable String distribution) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.APT);

    String artifactPath = "dists/" + distribution + "/Release";
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    if (!blobStore.exists(tenantId, repoId, artifactPath)) {
      return ResponseEntity.notFound().build();
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, artifactPath);
    long size = blobStore.size(tenantId, repoId, artifactPath);
    return ResponseEntity.ok()
        .contentType(MediaType.TEXT_PLAIN)
        .contentLength(size)
        .body(new org.springframework.core.io.InputStreamResource(data));
  }

  @Operation(summary = "获取InRelease文件", description = "获取APT仓库指定发行版的签名Release文件",
      operationId = "apt:inRelease")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "发行版不存在")
  })
  @GetMapping("/{repositoryName}/dists/{distribution}/InRelease")
  public ResponseEntity<?> inRelease(@PathVariable String repositoryName,
      @PathVariable String distribution) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.APT);

    String artifactPath = "dists/" + distribution + "/InRelease";
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    if (!blobStore.exists(tenantId, repoId, artifactPath)) {
      return ResponseEntity.notFound().build();
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, artifactPath);
    long size = blobStore.size(tenantId, repoId, artifactPath);
    return ResponseEntity.ok()
        .contentType(MediaType.TEXT_PLAIN)
        .contentLength(size)
        .body(new org.springframework.core.io.InputStreamResource(data));
  }

  @Operation(summary = "获取Packages索引", description = "获取指定发行版、组件和架构的软件包索引文件",
      operationId = "apt:packages")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "索引不存在")
  })
  @GetMapping("/{repositoryName}/dists/{distribution}/{component}/binary-{arch}/Packages")
  public ResponseEntity<?> packages(@PathVariable String repositoryName,
      @PathVariable String distribution, @PathVariable String component,
      @PathVariable String arch) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.APT);

    String artifactPath =
        "dists/" + distribution + "/" + component + "/binary-" + arch + "/Packages";
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    if (!blobStore.exists(tenantId, repoId, artifactPath)) {
      return ResponseEntity.notFound().build();
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, artifactPath);
    long size = blobStore.size(tenantId, repoId, artifactPath);
    return ResponseEntity.ok()
        .contentType(MediaType.TEXT_PLAIN)
        .contentLength(size)
        .body(new org.springframework.core.io.InputStreamResource(data));
  }

  @Operation(summary = "获取压缩Packages索引", description = "获取gzip压缩格式的软件包索引文件",
      operationId = "apt:packagesGz")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "索引不存在")
  })
  @GetMapping("/{repositoryName}/dists/{distribution}/{component}/binary-{arch}/Packages.gz")
  public ResponseEntity<?> packagesGz(@PathVariable String repositoryName,
      @PathVariable String distribution, @PathVariable String component,
      @PathVariable String arch) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.APT);

    String artifactPath =
        "dists/" + distribution + "/" + component + "/binary-" + arch + "/Packages.gz";
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

  @Operation(summary = "下载deb软件包", description = "从pool中下载.deb软件包文件",
      operationId = "apt:downloadDeb")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "下载成功"),
      @ApiResponse(responseCode = "404", description = "软件包不存在")
  })
  @GetMapping("/{repositoryName}/pool/{component}/{prefix}/{packageName}/{filename}")
  public ResponseEntity<?> downloadDeb(@PathVariable String repositoryName,
      @PathVariable String component, @PathVariable String prefix,
      @PathVariable String packageName, @PathVariable String filename) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.APT);

    String artifactPath =
        "pool/" + component + "/" + prefix + "/" + packageName + "/" + filename;
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    if (!blobStore.exists(tenantId, repoId, artifactPath)) {
      return ResponseEntity.notFound().build();
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, artifactPath);
    long size = blobStore.size(tenantId, repoId, artifactPath);
    return ResponseEntity.ok()
        .contentType(APPLICATION_DEB)
        .contentLength(size)
        .body(new org.springframework.core.io.InputStreamResource(data));
  }

  @Operation(summary = "上传deb软件包", description = "上传.deb软件包到hosted类型仓库",
      operationId = "apt:uploadDeb")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "上传成功"),
      @ApiResponse(responseCode = "400", description = "请求无效"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持上传")
  })
  @PutMapping("/{repositoryName}/pool/{filename}")
  public ResponseEntity<?> uploadDeb(@PathVariable String repositoryName,
      @PathVariable String filename, HttpServletRequest request) throws IOException {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.APT);
    validateHosted(repository);

    ArtifactFormatHandler handler = formatHandlerRegistry.getHandler(RepositoryFormat.APT);
    ValidationResult validation = handler.validateArtifact(null, filename);
    if (!validation.isValid()) {
      return ResponseEntity.badRequest()
          .body("{\"error\":\"" + String.join(", ", validation.getErrors()) + "\"}");
    }

    String artifactPath = "pool/" + filename;
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    try (InputStream inputStream = request.getInputStream()) {
      blobStore.store(tenantId, repoId, artifactPath, inputStream);
    }

    return ResponseEntity.status(HttpStatus.CREATED).build();
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
