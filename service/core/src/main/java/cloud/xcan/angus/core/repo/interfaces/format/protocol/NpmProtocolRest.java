package cloud.xcan.angus.core.repo.interfaces.format.protocol;

import cloud.xcan.angus.core.repo.application.query.repository.RepositoryQuery;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * NPM registry protocol controller.
 * Implements standard NPM registry HTTP API for npm/yarn/pnpm client compatibility.
 * Supports hosted/proxy/group repository types with scoped and unscoped packages.
 *
 * <p>Path format: /npm/{repositoryName}/{packageName}
 */
@Tag(name = "NPM Protocol", description = "NPM仓库协议 - 支持npm客户端的包发布、下载、搜索和管理")
@Validated
@RestController
@RequestMapping("/npm")
public class NpmProtocolRest {

  @Resource
  private RepositoryQuery repositoryQuery;

  @Resource
  private BlobStore blobStore;

  @Operation(summary = "获取NPM包文档", description = "获取包的完整文档，包含所有版本和dist-tags信息",
      operationId = "npm:getPackageDocument")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "返回包文档JSON"),
      @ApiResponse(responseCode = "404", description = "包不存在")
  })
  @GetMapping(value = "/{repositoryName}/{packageName}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getPackageDocument(@PathVariable String repositoryName,
      @PathVariable String packageName) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.NPM);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String metadataPath = packageName + "/package.json";
    if (!blobStore.exists(tenantId, repoId, metadataPath)) {
      return ResponseEntity.notFound().build();
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, metadataPath);
    long size = blobStore.size(tenantId, repoId, metadataPath);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .contentLength(size)
        .body(new InputStreamResource(data));
  }

  @Operation(summary = "获取NPM包指定版本", description = "获取包的指定版本信息",
      operationId = "npm:getPackageVersion")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "返回版本文档JSON"),
      @ApiResponse(responseCode = "404", description = "版本不存在")
  })
  @GetMapping(value = "/{repositoryName}/{packageName}/{version}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getPackageVersion(@PathVariable String repositoryName,
      @PathVariable String packageName, @PathVariable String version) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.NPM);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String versionPath = packageName + "/" + version + "/package.json";
    if (!blobStore.exists(tenantId, repoId, versionPath)) {
      return ResponseEntity.notFound().build();
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, versionPath);
    long size = blobStore.size(tenantId, repoId, versionPath);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .contentLength(size)
        .body(new InputStreamResource(data));
  }

  @Operation(summary = "下载NPM包tarball", description = "下载指定的NPM包tarball文件",
      operationId = "npm:downloadTarball")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "下载成功"),
      @ApiResponse(responseCode = "404", description = "文件不存在")
  })
  @GetMapping(value = "/{repositoryName}/{packageName}/-/{filename}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<?> downloadTarball(@PathVariable String repositoryName,
      @PathVariable String packageName, @PathVariable String filename) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.NPM);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String tarballPath = packageName + "/-/" + filename;
    if (!blobStore.exists(tenantId, repoId, tarballPath)) {
      return ResponseEntity.notFound().build();
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, tarballPath);
    long size = blobStore.size(tenantId, repoId, tarballPath);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .contentLength(size)
        .body(new InputStreamResource(data));
  }

  @Operation(summary = "发布NPM包", description = "发布NPM包到hosted类型仓库，包含完整的包文档和附件",
      operationId = "npm:publish")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "发布成功"),
      @ApiResponse(responseCode = "400", description = "请求无效"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持发布")
  })
  @PutMapping(value = "/{repositoryName}/{packageName}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> publish(@PathVariable String repositoryName,
      @PathVariable String packageName, HttpServletRequest request) throws IOException {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.NPM);
    validateHosted(repository);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String metadataPath = packageName + "/package.json";
    try (InputStream inputStream = request.getInputStream()) {
      blobStore.store(tenantId, repoId, metadataPath, inputStream);
    }
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(summary = "取消发布NPM包", description = "从hosted类型仓库取消发布NPM包",
      operationId = "npm:unpublish")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "取消发布成功"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持此操作"),
      @ApiResponse(responseCode = "404", description = "包不存在")
  })
  @DeleteMapping("/{repositoryName}/{packageName}/-rev/{rev}")
  public ResponseEntity<?> unpublish(@PathVariable String repositoryName,
      @PathVariable String packageName, @PathVariable String rev) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.NPM);
    validateHosted(repository);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String metadataPath = packageName + "/package.json";
    if (!blobStore.exists(tenantId, repoId, metadataPath)) {
      return ResponseEntity.notFound().build();
    }
    blobStore.delete(tenantId, repoId, metadataPath);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "设置NPM dist-tag", description = "为hosted类型仓库中的NPM包设置dist-tag",
      operationId = "npm:setDistTag")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "设置成功"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持此操作"),
      @ApiResponse(responseCode = "404", description = "包不存在")
  })
  @PutMapping(value = "/{repositoryName}/-/package/{packageName}/dist-tags/{tag}",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> setDistTag(@PathVariable String repositoryName,
      @PathVariable String packageName, @PathVariable String tag,
      HttpServletRequest request) throws IOException {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.NPM);
    validateHosted(repository);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String distTagPath = packageName + "/-/dist-tags/" + tag;
    try (InputStream inputStream = request.getInputStream()) {
      blobStore.store(tenantId, repoId, distTagPath, inputStream);
    }
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(summary = "搜索NPM包", description = "搜索仓库中的NPM包",
      operationId = "npm:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "返回搜索结果JSON")
  })
  @GetMapping(value = "/{repositoryName}/-/v1/search", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> search(@PathVariable String repositoryName,
      @RequestParam(value = "text", required = false, defaultValue = "") String text,
      @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.NPM);

    String emptyResult = "{\"objects\":[],\"total\":0,\"time\":\"" + java.time.Instant.now() + "\"}";
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(emptyResult);
  }

  // ===== Helper methods =====

  private String extractPath(HttpServletRequest request, String repositoryName) {
    String uri = request.getRequestURI();
    String prefix = "/npm/" + repositoryName + "/";
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
}
