package cloud.xcan.angus.core.repo.interfaces.format.protocol;

import cloud.xcan.angus.core.repo.application.query.repository.RepositoryQuery;
import cloud.xcan.angus.core.repo.domain.format.ArtifactFormatHandler;
import cloud.xcan.angus.core.repo.domain.format.FormatHandlerRegistry;
import cloud.xcan.angus.core.repo.domain.format.store.BlobStore;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.io.InputStream;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Go module proxy protocol controller.
 * Implements the GOPROXY protocol for go client compatibility.
 * Supports hosted/proxy/group repository types with module path and version based paths.
 *
 * <p>Path format: /go/{repositoryName}/github.com/user/repo/@v/v1.0.0.zip
 */
@Tag(name = "Go Protocol", description = "Go模块代理协议 - 支持Go客户端的模块版本查询、下载和元数据获取")
@Validated
@RestController
@RequestMapping("/go")
public class GoProtocolRest {

  private static final MediaType APPLICATION_ZIP = MediaType.parseMediaType("application/zip");

  @Resource
  private FormatHandlerRegistry formatHandlerRegistry;

  @Resource
  private RepositoryQuery repositoryQuery;

  @Resource
  private BlobStore blobStore;

  @Operation(summary = "列出模块可用版本", description = "获取指定Go模块的所有可用版本列表",
      operationId = "go:listVersions")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取版本列表成功"),
      @ApiResponse(responseCode = "404", description = "模块不存在")
  })
  @GetMapping("/{repositoryName}/**/@v/list")
  public ResponseEntity<?> listVersions(@Parameter(name = "repositoryName", description = "仓库名称") @PathVariable String repositoryName,
      HttpServletRequest request) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.GO);
    String modulePath = extractModulePath(request, repositoryName);

    String artifactPath = modulePath + "/@v/list";
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

  @Operation(summary = "获取版本信息", description = "获取指定Go模块版本的JSON元数据信息",
      operationId = "go:versionInfo")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取版本信息成功"),
      @ApiResponse(responseCode = "404", description = "版本不存在")
  })
  @GetMapping("/{repositoryName}/**/@v/{version}.info")
  public ResponseEntity<?> versionInfo(@Parameter(name = "repositoryName", description = "仓库名称") @PathVariable String repositoryName,
      @Parameter(name = "version", description = "版本号") @PathVariable String version, HttpServletRequest request) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.GO);
    String modulePath = extractModulePath(request, repositoryName);

    String artifactPath = modulePath + "/@v/" + version + ".info";
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    if (!blobStore.exists(tenantId, repoId, artifactPath)) {
      return ResponseEntity.notFound().build();
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, artifactPath);
    long size = blobStore.size(tenantId, repoId, artifactPath);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .contentLength(size)
        .body(new org.springframework.core.io.InputStreamResource(data));
  }

  @Operation(summary = "获取go.mod文件", description = "获取指定Go模块版本的go.mod文件",
      operationId = "go:goMod")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取go.mod成功"),
      @ApiResponse(responseCode = "404", description = "版本不存在")
  })
  @GetMapping("/{repositoryName}/**/@v/{version}.mod")
  public ResponseEntity<?> goMod(@Parameter(name = "repositoryName", description = "仓库名称") @PathVariable String repositoryName,
      @Parameter(name = "version", description = "版本号") @PathVariable String version, HttpServletRequest request) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.GO);
    String modulePath = extractModulePath(request, repositoryName);

    String artifactPath = modulePath + "/@v/" + version + ".mod";
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

  @Operation(summary = "下载模块源码包", description = "下载指定Go模块版本的源码压缩包(.zip)",
      operationId = "go:downloadZip")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "下载成功"),
      @ApiResponse(responseCode = "404", description = "版本不存在")
  })
  @GetMapping("/{repositoryName}/**/@v/{version}.zip")
  public ResponseEntity<?> downloadZip(@Parameter(name = "repositoryName", description = "仓库名称") @PathVariable String repositoryName,
      @Parameter(name = "version", description = "版本号") @PathVariable String version, HttpServletRequest request) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.GO);
    String modulePath = extractModulePath(request, repositoryName);

    String artifactPath = modulePath + "/@v/" + version + ".zip";
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    if (!blobStore.exists(tenantId, repoId, artifactPath)) {
      return ResponseEntity.notFound().build();
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, artifactPath);
    long size = blobStore.size(tenantId, repoId, artifactPath);
    return ResponseEntity.ok()
        .contentType(APPLICATION_ZIP)
        .contentLength(size)
        .body(new org.springframework.core.io.InputStreamResource(data));
  }

  @Operation(summary = "获取最新版本信息", description = "获取指定Go模块的最新版本信息",
      operationId = "go:latest")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取最新版本成功"),
      @ApiResponse(responseCode = "404", description = "模块不存在")
  })
  @GetMapping("/{repositoryName}/**/@latest")
  public ResponseEntity<?> latest(@Parameter(name = "repositoryName", description = "仓库名称") @PathVariable String repositoryName,
      HttpServletRequest request) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.GO);
    String modulePath = extractModulePath(request, repositoryName);

    String artifactPath = modulePath + "/@latest";
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    if (!blobStore.exists(tenantId, repoId, artifactPath)) {
      return ResponseEntity.notFound().build();
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, artifactPath);
    long size = blobStore.size(tenantId, repoId, artifactPath);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .contentLength(size)
        .body(new org.springframework.core.io.InputStreamResource(data));
  }

  // ===== Helper methods =====

  private String extractModulePath(HttpServletRequest request, String repositoryName) {
    String uri = request.getRequestURI();
    String prefix = "/go/" + repositoryName + "/";
    int idx = uri.indexOf(prefix);
    if (idx < 0) {
      return uri;
    }
    String remainder = uri.substring(idx + prefix.length());
    // Strip the trailing @v/... or @latest suffix to get the module path
    int atIdx = remainder.indexOf("/@");
    if (atIdx >= 0) {
      return remainder.substring(0, atIdx);
    }
    return remainder;
  }

  private void validateFormat(RepoEntity repository, RepositoryFormat expectedFormat) {
    if (repository.getFormat() != expectedFormat) {
      throw new IllegalArgumentException(
          "Repository '" + repository.getName() + "' is not a " + expectedFormat.getValue()
              + " repository");
    }
  }

}
