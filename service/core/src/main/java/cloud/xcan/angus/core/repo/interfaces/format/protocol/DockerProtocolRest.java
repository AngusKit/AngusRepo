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
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Docker Registry V2 protocol controller.
 * Implements Docker Registry HTTP API V2 for docker client compatibility.
 * Supports hosted/proxy/group repository types with OCI image format.
 *
 * <p>Path format: /v2/{name}/manifests/{reference}
 */
@Tag(name = "Docker Protocol", description = "Docker Registry V2协议 - 支持Docker客户端的镜像推送、拉取和管理")
@Validated
@RestController
@RequestMapping("/v2")
public class DockerProtocolRest {

  private static final String API_VERSION_HEADER = "Docker-Distribution-Api-Version";
  private static final String API_VERSION = "registry/2.0";
  private static final String CONTENT_DIGEST_HEADER = "Docker-Content-Digest";
  private static final String UPLOAD_UUID_HEADER = "Docker-Upload-UUID";
  private static final MediaType MANIFEST_V2_JSON =
      MediaType.parseMediaType("application/vnd.docker.distribution.manifest.v2+json");

  @Resource
  private RepositoryQuery repositoryQuery;

  @Resource
  private BlobStore blobStore;

  // ===== Base Endpoints =====

  @Operation(summary = "Docker API版本检查",
      description = "检查Docker Registry V2 API可用性，返回Docker-Distribution-Api-Version头",
      operationId = "docker:version-check")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "API可用")
  })
  @GetMapping({"", "/"})
  public ResponseEntity<?> versionCheck() {
    return ResponseEntity.ok()
        .header(API_VERSION_HEADER, API_VERSION)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{}");
  }

  @Operation(summary = "列出Docker仓库",
      description = "列出Registry中所有可用的Docker镜像仓库名称",
      operationId = "docker:catalog")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "列出成功")
  })
  @GetMapping("/_catalog")
  public ResponseEntity<?> catalog() {
    return ResponseEntity.ok()
        .header(API_VERSION_HEADER, API_VERSION)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\"repositories\":[]}");
  }

  // ===== Tag Endpoints =====

  @Operation(summary = "列出镜像标签",
      description = "列出指定Docker镜像的所有可用标签",
      operationId = "docker:list-tags")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "列出成功"),
      @ApiResponse(responseCode = "404", description = "镜像不存在")
  })
  @GetMapping("/{name}/tags/list")
  public ResponseEntity<?> listTags(@Parameter(name = "name", description = "name") @PathVariable String name) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(name);
    validateFormat(repository, RepositoryFormat.DOCKER);
    return ResponseEntity.ok()
        .header(API_VERSION_HEADER, API_VERSION)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\"name\":\"" + name + "\",\"tags\":[]}");
  }

  // ===== Manifest Endpoints =====

  @Operation(summary = "检查镜像Manifest是否存在",
      description = "检查指定引用的Docker镜像Manifest是否存在",
      operationId = "docker:check-manifest")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Manifest存在"),
      @ApiResponse(responseCode = "404", description = "Manifest不存在")
  })
  @RequestMapping(value = "/{name}/manifests/{reference}", method = RequestMethod.HEAD)
  public ResponseEntity<?> checkManifest(@Parameter(name = "name", description = "name") @PathVariable String name,
      @Parameter(name = "reference", description = "reference") @PathVariable String reference) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(name);
    validateFormat(repository, RepositoryFormat.DOCKER);
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String artifactPath = "manifests/" + reference;

    if (blobStore.exists(tenantId, repoId, artifactPath)) {
      long size = blobStore.size(tenantId, repoId, artifactPath);
      return ResponseEntity.ok()
          .header(API_VERSION_HEADER, API_VERSION)
          .header(CONTENT_DIGEST_HEADER, reference)
          .contentType(MANIFEST_V2_JSON)
          .contentLength(size)
          .build();
    }
    return ResponseEntity.notFound().build();
  }

  @Operation(summary = "获取镜像Manifest",
      description = "获取指定引用的Docker镜像Manifest内容",
      operationId = "docker:get-manifest")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "Manifest不存在")
  })
  @GetMapping("/{name}/manifests/{reference}")
  public ResponseEntity<?> getManifest(@Parameter(name = "name", description = "name") @PathVariable String name,
      @Parameter(name = "reference", description = "reference") @PathVariable String reference) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(name);
    validateFormat(repository, RepositoryFormat.DOCKER);
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String artifactPath = "manifests/" + reference;

    if (!blobStore.exists(tenantId, repoId, artifactPath)) {
      return ResponseEntity.notFound().build();
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, artifactPath);
    long size = blobStore.size(tenantId, repoId, artifactPath);
    return ResponseEntity.ok()
        .header(API_VERSION_HEADER, API_VERSION)
        .header(CONTENT_DIGEST_HEADER, reference)
        .contentType(MANIFEST_V2_JSON)
        .contentLength(size)
        .body(new InputStreamResource(data));
  }

  @Operation(summary = "上传镜像Manifest",
      description = "上传Docker镜像Manifest到hosted类型仓库",
      operationId = "docker:upload-manifest")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "上传成功"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持上传")
  })
  @PutMapping("/{name}/manifests/{reference}")
  public ResponseEntity<?> uploadManifest(@Parameter(name = "name", description = "name") @PathVariable String name,
      @Parameter(name = "reference", description = "reference") @PathVariable String reference, HttpServletRequest request) throws IOException {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(name);
    validateFormat(repository, RepositoryFormat.DOCKER);
    validateHosted(repository);
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String artifactPath = "manifests/" + reference;

    try (InputStream inputStream = request.getInputStream()) {
      blobStore.store(tenantId, repoId, artifactPath, inputStream);
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .header(API_VERSION_HEADER, API_VERSION)
        .header(CONTENT_DIGEST_HEADER, reference)
        .header("Location", "/v2/" + name + "/manifests/" + reference)
        .contentType(MANIFEST_V2_JSON)
        .build();
  }

  @Operation(summary = "删除镜像Manifest",
      description = "从hosted类型仓库删除指定的Docker镜像Manifest",
      operationId = "docker:delete-manifest")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "删除成功"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持删除"),
      @ApiResponse(responseCode = "404", description = "Manifest不存在")
  })
  @DeleteMapping("/{name}/manifests/{reference}")
  public ResponseEntity<?> deleteManifest(@Parameter(name = "name", description = "name") @PathVariable String name,
      @Parameter(name = "reference", description = "reference") @PathVariable String reference) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(name);
    validateFormat(repository, RepositoryFormat.DOCKER);
    validateHosted(repository);
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String artifactPath = "manifests/" + reference;

    if (!blobStore.exists(tenantId, repoId, artifactPath)) {
      return ResponseEntity.notFound().build();
    }
    blobStore.delete(tenantId, repoId, artifactPath);
    return ResponseEntity.accepted().build();
  }

  // ===== Blob Endpoints =====

  @Operation(summary = "检查Blob是否存在",
      description = "检查指定摘要的Docker镜像层Blob是否存在",
      operationId = "docker:check-blob")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Blob存在"),
      @ApiResponse(responseCode = "404", description = "Blob不存在")
  })
  @RequestMapping(value = "/{name}/blobs/{digest}", method = RequestMethod.HEAD)
  public ResponseEntity<?> checkBlob(@Parameter(name = "name", description = "name") @PathVariable String name,
      @Parameter(name = "digest", description = "digest") @PathVariable String digest) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(name);
    validateFormat(repository, RepositoryFormat.DOCKER);
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String artifactPath = "blobs/" + digest;

    if (blobStore.exists(tenantId, repoId, artifactPath)) {
      long size = blobStore.size(tenantId, repoId, artifactPath);
      return ResponseEntity.ok()
          .header(API_VERSION_HEADER, API_VERSION)
          .header(CONTENT_DIGEST_HEADER, digest)
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .contentLength(size)
          .build();
    }
    return ResponseEntity.notFound().build();
  }

  @Operation(summary = "下载Blob",
      description = "下载指定摘要的Docker镜像层Blob数据",
      operationId = "docker:download-blob")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "下载成功"),
      @ApiResponse(responseCode = "404", description = "Blob不存在")
  })
  @GetMapping("/{name}/blobs/{digest}")
  public ResponseEntity<?> downloadBlob(@Parameter(name = "name", description = "name") @PathVariable String name,
      @Parameter(name = "digest", description = "digest") @PathVariable String digest) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(name);
    validateFormat(repository, RepositoryFormat.DOCKER);
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String artifactPath = "blobs/" + digest;

    if (!blobStore.exists(tenantId, repoId, artifactPath)) {
      return ResponseEntity.notFound().build();
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, artifactPath);
    long size = blobStore.size(tenantId, repoId, artifactPath);
    return ResponseEntity.ok()
        .header(API_VERSION_HEADER, API_VERSION)
        .header(CONTENT_DIGEST_HEADER, digest)
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .contentLength(size)
        .body(new InputStreamResource(data));
  }

  @Operation(summary = "删除Blob",
      description = "从hosted类型仓库删除指定摘要的Docker镜像层Blob",
      operationId = "docker:delete-blob")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "删除成功"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持删除"),
      @ApiResponse(responseCode = "404", description = "Blob不存在")
  })
  @DeleteMapping("/{name}/blobs/{digest}")
  public ResponseEntity<?> deleteBlob(@Parameter(name = "name", description = "name") @PathVariable String name,
      @Parameter(name = "digest", description = "digest") @PathVariable String digest) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(name);
    validateFormat(repository, RepositoryFormat.DOCKER);
    validateHosted(repository);
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String artifactPath = "blobs/" + digest;

    if (!blobStore.exists(tenantId, repoId, artifactPath)) {
      return ResponseEntity.notFound().build();
    }
    blobStore.delete(tenantId, repoId, artifactPath);
    return ResponseEntity.accepted().build();
  }

  // ===== Upload Endpoints =====

  @Operation(summary = "开始Blob上传",
      description = "初始化Docker镜像层Blob的上传会话，返回上传UUID",
      operationId = "docker:start-upload")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "上传会话已创建"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持上传")
  })
  @PostMapping(value = {"/{name}/blobs/uploads", "/{name}/blobs/uploads/"})
  public ResponseEntity<?> startUpload(@Parameter(name = "name", description = "name") @PathVariable String name) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(name);
    validateFormat(repository, RepositoryFormat.DOCKER);
    validateHosted(repository);
    String uuid = UUID.randomUUID().toString();

    return ResponseEntity.accepted()
        .header(API_VERSION_HEADER, API_VERSION)
        .header("Location", "/v2/" + name + "/blobs/uploads/" + uuid)
        .header("Range", "0-0")
        .header(UPLOAD_UUID_HEADER, uuid)
        .build();
  }

  @Operation(summary = "上传Blob数据块",
      description = "上传Docker镜像层Blob的数据块到指定的上传会话",
      operationId = "docker:upload-chunk")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "数据块上传成功"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持上传")
  })
  @PatchMapping("/{name}/blobs/uploads/{uuid}")
  public ResponseEntity<?> uploadChunk(@Parameter(name = "name", description = "name") @PathVariable String name,
      @Parameter(name = "uuid", description = "uuid") @PathVariable String uuid, HttpServletRequest request) throws IOException {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(name);
    validateFormat(repository, RepositoryFormat.DOCKER);
    validateHosted(repository);
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String chunkPath = "uploads/" + uuid;

    try (InputStream inputStream = request.getInputStream()) {
      blobStore.store(tenantId, repoId, chunkPath, inputStream);
    }

    long size = blobStore.size(tenantId, repoId, chunkPath);
    return ResponseEntity.accepted()
        .header(API_VERSION_HEADER, API_VERSION)
        .header("Location", "/v2/" + name + "/blobs/uploads/" + uuid)
        .header("Range", "0-" + (size - 1))
        .header(UPLOAD_UUID_HEADER, uuid)
        .build();
  }

  @Operation(summary = "完成Blob上传",
      description = "完成Docker镜像层Blob的上传会话，提供最终摘要验证",
      operationId = "docker:complete-upload")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "上传完成"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持上传")
  })
  @PutMapping("/{name}/blobs/uploads/{uuid}")
  public ResponseEntity<?> completeUpload(@Parameter(name = "name", description = "name") @PathVariable String name,
      @Parameter(name = "uuid", description = "uuid") @PathVariable String uuid, @RequestParam String digest,
      HttpServletRequest request) throws IOException {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(name);
    validateFormat(repository, RepositoryFormat.DOCKER);
    validateHosted(repository);
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    String blobPath = "blobs/" + digest;

    try (InputStream inputStream = request.getInputStream()) {
      blobStore.store(tenantId, repoId, blobPath, inputStream);
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .header(API_VERSION_HEADER, API_VERSION)
        .header("Location", "/v2/" + name + "/blobs/" + digest)
        .header(CONTENT_DIGEST_HEADER, digest)
        .build();
  }

  // ===== Helper methods =====

  private void validateFormat(RepoEntity repository, RepositoryFormat expectedFormat) {
    if (repository.getFormat() != expectedFormat) {
      throw new IllegalArgumentException(
          "Repository '" + repository.getName() + "' is not a "
              + expectedFormat.getValue() + " repository");
    }
  }

  private void validateHosted(RepoEntity repository) {
    if (repository.getType() != RepositoryType.HOSTED) {
      throw new IllegalStateException(
          "Upload/delete operations are only allowed on hosted repositories");
    }
  }
}
