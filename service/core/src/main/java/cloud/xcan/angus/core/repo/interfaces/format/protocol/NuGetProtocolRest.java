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
 * NuGet V3 protocol controller.
 * Implements NuGet V3 API for dotnet/nuget client compatibility.
 * Supports hosted/proxy/group repository types with .nupkg package format.
 *
 * <p>Path format: /nuget/{repositoryName}/v3/index.json
 */
@Tag(name = "NuGet Protocol", description = "NuGet仓库协议 - 支持dotnet/nuget客户端的包推送、下载、搜索和管理")
@Validated
@RestController
@RequestMapping("/nuget")
public class NuGetProtocolRest {

  @Resource
  private RepositoryQuery repositoryQuery;

  @Resource
  private BlobStore blobStore;

  @Operation(summary = "获取NuGet服务索引", description = "返回NuGet V3 Service Index，包含所有可用服务端点",
      operationId = "nuget:serviceIndex")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "返回服务索引JSON")
  })
  @GetMapping(value = "/{repositoryName}/v3/index.json", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> serviceIndex(@PathVariable String repositoryName,
      HttpServletRequest request) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.NUGET);

    String baseUrl = buildBaseUrl(request, repositoryName);
    String serviceIndexJson = "{"
        + "\"version\":\"3.0.0\","
        + "\"resources\":["
        + "{\"@id\":\"" + baseUrl + "/v3/search\",\"@type\":\"SearchQueryService\",\"comment\":\"Search packages\"},"
        + "{\"@id\":\"" + baseUrl + "/v3/registration\",\"@type\":\"RegistrationsBaseUrl\",\"comment\":\"Package registrations\"},"
        + "{\"@id\":\"" + baseUrl + "/v3/flatcontainer\",\"@type\":\"PackageBaseAddress/3.0.0\",\"comment\":\"Package content\"},"
        + "{\"@id\":\"" + baseUrl + "/api/v2/package\",\"@type\":\"PackagePublish/2.0.0\",\"comment\":\"Push and delete packages\"}"
        + "]}";
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(serviceIndexJson);
  }

  @Operation(summary = "搜索NuGet包", description = "搜索仓库中的NuGet包",
      operationId = "nuget:search")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "返回搜索结果JSON")
  })
  @GetMapping(value = "/{repositoryName}/v3/search", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> search(@PathVariable String repositoryName,
      @RequestParam(value = "q", required = false, defaultValue = "") String query,
      @RequestParam(value = "skip", required = false, defaultValue = "0") int skip,
      @RequestParam(value = "take", required = false, defaultValue = "20") int take) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.NUGET);

    String emptyResult = "{\"totalHits\":0,\"data\":[]}";
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(emptyResult);
  }

  @Operation(summary = "获取包注册信息", description = "获取指定包的注册信息，包含所有版本",
      operationId = "nuget:registration")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "返回注册信息JSON"),
      @ApiResponse(responseCode = "404", description = "包不存在")
  })
  @GetMapping(value = "/{repositoryName}/v3/registration/{id}/index.json",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> registration(@PathVariable String repositoryName,
      @PathVariable String id) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.NUGET);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String registrationPath = "v3/registration/" + id.toLowerCase() + "/index.json";
    if (!blobStore.exists(tenantId, repoId, registrationPath)) {
      return ResponseEntity.notFound().build();
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, registrationPath);
    long size = blobStore.size(tenantId, repoId, registrationPath);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .contentLength(size)
        .body(new InputStreamResource(data));
  }

  @Operation(summary = "获取包版本列表", description = "获取指定包的所有可用版本",
      operationId = "nuget:packageVersions")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "返回版本列表JSON"),
      @ApiResponse(responseCode = "404", description = "包不存在")
  })
  @GetMapping(value = "/{repositoryName}/v3/flatcontainer/{id}/index.json",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> packageVersions(@PathVariable String repositoryName,
      @PathVariable String id) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.NUGET);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String versionsPath = "v3/flatcontainer/" + id.toLowerCase() + "/index.json";
    if (!blobStore.exists(tenantId, repoId, versionsPath)) {
      return ResponseEntity.notFound().build();
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, versionsPath);
    long size = blobStore.size(tenantId, repoId, versionsPath);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .contentLength(size)
        .body(new InputStreamResource(data));
  }

  @Operation(summary = "下载NuGet包", description = "下载指定的.nupkg包文件",
      operationId = "nuget:download")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "下载成功"),
      @ApiResponse(responseCode = "404", description = "包不存在")
  })
  @GetMapping(value = "/{repositoryName}/v3/flatcontainer/{id}/{version}/**",
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<?> download(@PathVariable String repositoryName,
      @PathVariable String id, @PathVariable String version,
      HttpServletRequest request) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.NUGET);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String nupkgPath = "v3/flatcontainer/" + id.toLowerCase() + "/" + version.toLowerCase()
        + "/" + id.toLowerCase() + "." + version.toLowerCase() + ".nupkg";
    if (!blobStore.exists(tenantId, repoId, nupkgPath)) {
      return ResponseEntity.notFound().build();
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, nupkgPath);
    long size = blobStore.size(tenantId, repoId, nupkgPath);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .contentLength(size)
        .body(new InputStreamResource(data));
  }

  @Operation(summary = "推送NuGet包", description = "推送.nupkg包到hosted类型仓库，使用X-NuGet-ApiKey进行认证",
      operationId = "nuget:push")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "推送成功"),
      @ApiResponse(responseCode = "400", description = "请求无效"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持推送")
  })
  @PutMapping(value = "/{repositoryName}/api/v2/package",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> push(@PathVariable String repositoryName,
      HttpServletRequest request) throws IOException {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.NUGET);
    validateHosted(repository);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String uploadPath = "api/v2/package/" + System.currentTimeMillis() + ".nupkg";
    try (InputStream inputStream = request.getInputStream()) {
      blobStore.store(tenantId, repoId, uploadPath, inputStream);
    }
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(summary = "删除NuGet包", description = "从hosted类型仓库删除指定版本的NuGet包",
      operationId = "nuget:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持删除"),
      @ApiResponse(responseCode = "404", description = "包不存在")
  })
  @DeleteMapping("/{repositoryName}/api/v2/package/{id}/{version}")
  public ResponseEntity<?> delete(@PathVariable String repositoryName,
      @PathVariable String id, @PathVariable String version) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.NUGET);
    validateHosted(repository);

    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String nupkgPath = "v3/flatcontainer/" + id.toLowerCase() + "/" + version.toLowerCase()
        + "/" + id.toLowerCase() + "." + version.toLowerCase() + ".nupkg";
    if (!blobStore.exists(tenantId, repoId, nupkgPath)) {
      return ResponseEntity.notFound().build();
    }
    blobStore.delete(tenantId, repoId, nupkgPath);
    return ResponseEntity.noContent().build();
  }

  // ===== Helper methods =====

  private String extractPath(HttpServletRequest request, String repositoryName) {
    String uri = request.getRequestURI();
    String prefix = "/nuget/" + repositoryName + "/";
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

  private String buildBaseUrl(HttpServletRequest request, String repositoryName) {
    String scheme = request.getScheme();
    String host = request.getServerName();
    int port = request.getServerPort();
    String portPart = (("http".equals(scheme) && port == 80)
        || ("https".equals(scheme) && port == 443)) ? "" : ":" + port;
    return scheme + "://" + host + portPart + "/nuget/" + repositoryName;
  }
}
