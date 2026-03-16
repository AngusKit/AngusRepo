package cloud.xcan.angus.core.repo.interfaces.format.protocol;

import cloud.xcan.angus.core.repo.application.query.repository.RepositoryQuery;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * PyPI repository protocol controller.
 * Implements PEP 503 Simple Repository API and legacy upload for pip/twine client compatibility.
 * Supports hosted/proxy/group repository types with Python package format.
 *
 * <p>Path format: /pypi/{repositoryName}/simple/{packageName}/
 */
@Tag(name = "PyPI Protocol", description = "PyPI仓库协议 - 支持pip/twine客户端的包上传、下载和索引查询")
@Validated
@RestController
@RequestMapping("/pypi")
public class PyPIProtocolRest {

  private static final MediaType TEXT_HTML = MediaType.TEXT_HTML;

  @Resource
  private RepositoryQuery repositoryQuery;

  @Resource
  private BlobStore blobStore;

  @Operation(summary = "获取Simple索引", description = "获取Simple Repository根索引页面，列出所有包名",
      operationId = "pypi:simpleIndex")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "返回HTML索引页面")
  })
  @GetMapping(value = "/{repositoryName}/simple/", produces = MediaType.TEXT_HTML_VALUE)
  public ResponseEntity<?> simpleIndex(@Parameter(name = "repositoryName", description = "repositoryName") @PathVariable String repositoryName) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.PYPI);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String indexPath = "simple/index.html";
    if (blobStore.exists(tenantId, repoId, indexPath)) {
      InputStream data = blobStore.retrieve(tenantId, repoId, indexPath);
      long size = blobStore.size(tenantId, repoId, indexPath);
      return ResponseEntity.ok()
          .contentType(TEXT_HTML)
          .contentLength(size)
          .body(new InputStreamResource(data));
    }
    String emptyIndex = "<!DOCTYPE html><html><head><title>Simple Index</title></head>"
        + "<body><h1>Simple Index</h1></body></html>";
    return ResponseEntity.ok()
        .contentType(TEXT_HTML)
        .body(emptyIndex);
  }

  @Operation(summary = "获取包页面", description = "获取指定包的Simple页面，列出所有可下载的文件链接",
      operationId = "pypi:packagePage")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "返回HTML包页面"),
      @ApiResponse(responseCode = "404", description = "包不存在")
  })
  @GetMapping(value = "/{repositoryName}/simple/{packageName}/", produces = MediaType.TEXT_HTML_VALUE)
  public ResponseEntity<?> packagePage(@Parameter(name = "repositoryName", description = "repositoryName") @PathVariable String repositoryName,
      @Parameter(name = "packageName", description = "packageName") @PathVariable String packageName) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.PYPI);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String packageIndexPath = "simple/" + normalizePackageName(packageName) + "/index.html";
    if (blobStore.exists(tenantId, repoId, packageIndexPath)) {
      InputStream data = blobStore.retrieve(tenantId, repoId, packageIndexPath);
      long size = blobStore.size(tenantId, repoId, packageIndexPath);
      return ResponseEntity.ok()
          .contentType(TEXT_HTML)
          .contentLength(size)
          .body(new InputStreamResource(data));
    }
    return ResponseEntity.notFound().build();
  }

  @Operation(summary = "上传Python包", description = "通过twine上传Python包到hosted类型仓库",
      operationId = "pypi:upload")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "上传成功"),
      @ApiResponse(responseCode = "400", description = "请求无效"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持上传")
  })
  @PostMapping(value = "/{repositoryName}/legacy/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> upload(@Parameter(name = "repositoryName", description = "repositoryName") @PathVariable String repositoryName,
      @RequestParam("content") MultipartFile content,
      @RequestParam(value = "name", required = false) String name,
      @RequestParam(value = "version", required = false) String version) throws IOException {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.PYPI);
    validateHosted(repository);

    String originalFilename = content.getOriginalFilename();
    if (originalFilename == null || originalFilename.isBlank()) {
      return ResponseEntity.badRequest()
          .body("{\"error\":\"Missing filename in upload\"}");
    }

    String packageName = name != null ? normalizePackageName(name) : extractPackageName(originalFilename);
    String packageVersion = version != null ? version : extractVersion(originalFilename);
    String storagePath = "packages/" + packageName + "/" + packageVersion + "/" + originalFilename;

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    try (InputStream inputStream = content.getInputStream()) {
      blobStore.store(tenantId, repoId, storagePath, inputStream);
    }
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(summary = "下载Python包文件", description = "下载指定的Python包文件",
      operationId = "pypi:download")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "下载成功"),
      @ApiResponse(responseCode = "404", description = "文件不存在")
  })
  @GetMapping(value = "/{repositoryName}/packages/{packageName}/{version}/{filename}",
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<?> download(@Parameter(name = "repositoryName", description = "repositoryName") @PathVariable String repositoryName,
      @Parameter(name = "packageName", description = "packageName") @PathVariable String packageName, @Parameter(name = "version", description = "version") @PathVariable String version,
      @Parameter(name = "filename", description = "filename") @PathVariable String filename) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.PYPI);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String filePath = "packages/" + packageName + "/" + version + "/" + filename;
    if (!blobStore.exists(tenantId, repoId, filePath)) {
      return ResponseEntity.notFound().build();
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, filePath);
    long size = blobStore.size(tenantId, repoId, filePath);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .contentLength(size)
        .body(new InputStreamResource(data));
  }

  @Operation(summary = "获取包JSON元数据", description = "获取指定包的JSON格式元数据信息",
      operationId = "pypi:packageJson")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "返回包元数据JSON"),
      @ApiResponse(responseCode = "404", description = "包不存在")
  })
  @GetMapping(value = "/{repositoryName}/pypi/{packageName}/json", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> packageJson(@Parameter(name = "repositoryName", description = "repositoryName") @PathVariable String repositoryName,
      @Parameter(name = "packageName", description = "packageName") @PathVariable String packageName) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.PYPI);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String metadataPath = "pypi/" + normalizePackageName(packageName) + "/json";
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

  @Operation(summary = "获取包版本JSON元数据", description = "获取指定包指定版本的JSON格式元数据信息",
      operationId = "pypi:versionJson")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "返回版本元数据JSON"),
      @ApiResponse(responseCode = "404", description = "版本不存在")
  })
  @GetMapping(value = "/{repositoryName}/pypi/{packageName}/{version}/json",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> versionJson(@Parameter(name = "repositoryName", description = "repositoryName") @PathVariable String repositoryName,
      @Parameter(name = "packageName", description = "packageName") @PathVariable String packageName, @Parameter(name = "version", description = "version") @PathVariable String version) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.PYPI);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String metadataPath = "pypi/" + normalizePackageName(packageName) + "/" + version + "/json";
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

  // ===== Helper methods =====

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

  private String normalizePackageName(String name) {
    return name.toLowerCase().replaceAll("[\\-_.]+", "-");
  }

  private String extractPackageName(String filename) {
    int dashIdx = filename.indexOf('-');
    if (dashIdx > 0) {
      return normalizePackageName(filename.substring(0, dashIdx));
    }
    return normalizePackageName(filename);
  }

  private String extractVersion(String filename) {
    int dashIdx = filename.indexOf('-');
    if (dashIdx > 0) {
      String rest = filename.substring(dashIdx + 1);
      int extIdx = rest.indexOf(".tar.");
      if (extIdx < 0) {
        extIdx = rest.lastIndexOf('.');
      }
      if (extIdx > 0) {
        return rest.substring(0, extIdx);
      }
      return rest;
    }
    return "0.0.0";
  }
}
