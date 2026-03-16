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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * YUM/DNF repository protocol controller.
 * Implements the YUM repository protocol for yum/dnf client compatibility.
 * Supports hosted/proxy/group repository types with repodata and RPM package paths.
 *
 * <p>Path format: /yum/{repositoryName}/Packages/a/ansible-2.9.0-1.el8.noarch.rpm
 */
@Tag(name = "YUM Protocol", description = "YUM/DNF仓库协议 - 支持yum/dnf客户端的RPM包上传、下载、删除和元数据查询")
@Validated
@RestController
@RequestMapping("/yum")
public class YumProtocolRest {

  private static final MediaType APPLICATION_GZIP = MediaType.parseMediaType("application/gzip");
  private static final MediaType APPLICATION_X_RPM = MediaType.parseMediaType("application/x-rpm");

  @Resource
  private FormatHandlerRegistry formatHandlerRegistry;

  @Resource
  private RepositoryQuery repositoryQuery;

  @Resource
  private BlobStore blobStore;

  @Operation(summary = "获取仓库元数据索引", description = "获取YUM仓库的repomd.xml元数据索引文件",
      operationId = "yum:repomd")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "仓库元数据不存在")
  })
  @GetMapping("/{repositoryName}/repodata/repomd.xml")
  public ResponseEntity<?> repomd(@Parameter(name = "repositoryName", description = "repositoryName") @PathVariable String repositoryName) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.YUM);

    String artifactPath = "repodata/repomd.xml";
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    if (!blobStore.exists(tenantId, repoId, artifactPath)) {
      ArtifactFormatHandler handler = formatHandlerRegistry.getHandler(RepositoryFormat.YUM);
      byte[] index = handler.generateIndex(repository);
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_XML)
          .contentLength(index.length)
          .body(index);
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, artifactPath);
    long size = blobStore.size(tenantId, repoId, artifactPath);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_XML)
        .contentLength(size)
        .body(new org.springframework.core.io.InputStreamResource(data));
  }

  @Operation(summary = "获取元数据文件", description = "获取repodata目录下的元数据文件(如primary.xml.gz、filelists.xml.gz等)",
      operationId = "yum:repodata")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "元数据文件不存在")
  })
  @GetMapping("/{repositoryName}/repodata/{filename}")
  public ResponseEntity<?> repodata(@Parameter(name = "repositoryName", description = "repositoryName") @PathVariable String repositoryName,
      @Parameter(name = "filename", description = "filename") @PathVariable String filename) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.YUM);

    String artifactPath = "repodata/" + filename;
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

  @Operation(summary = "下载RPM包", description = "下载指定的.rpm软件包文件",
      operationId = "yum:downloadRpm")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "下载成功"),
      @ApiResponse(responseCode = "404", description = "RPM包不存在")
  })
  @GetMapping("/{repositoryName}/Packages/{letter}/{filename}")
  public ResponseEntity<?> downloadRpm(@Parameter(name = "repositoryName", description = "repositoryName") @PathVariable String repositoryName,
      @Parameter(name = "letter", description = "letter") @PathVariable String letter, @Parameter(name = "filename", description = "filename") @PathVariable String filename) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.YUM);

    String artifactPath = "Packages/" + letter + "/" + filename;
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    if (!blobStore.exists(tenantId, repoId, artifactPath)) {
      return ResponseEntity.notFound().build();
    }
    InputStream data = blobStore.retrieve(tenantId, repoId, artifactPath);
    long size = blobStore.size(tenantId, repoId, artifactPath);
    return ResponseEntity.ok()
        .contentType(APPLICATION_X_RPM)
        .contentLength(size)
        .body(new org.springframework.core.io.InputStreamResource(data));
  }

  @Operation(summary = "上传RPM包", description = "上传.rpm软件包到hosted类型仓库",
      operationId = "yum:uploadRpm")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "上传成功"),
      @ApiResponse(responseCode = "400", description = "请求无效"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持上传")
  })
  @PutMapping(value = "/{repositoryName}/upload",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> uploadRpm(@Parameter(name = "repositoryName", description = "repositoryName") @PathVariable String repositoryName,
      @RequestParam("file") MultipartFile file) throws IOException {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.YUM);
    validateHosted(repository);

    String fileName = file.getOriginalFilename();
    if (fileName == null || fileName.isBlank()) {
      return ResponseEntity.badRequest()
          .body("{\"error\":\"RPM file name is required\"}");
    }

    ArtifactFormatHandler handler = formatHandlerRegistry.getHandler(RepositoryFormat.YUM);
    ValidationResult validation = handler.validateArtifact(null, fileName);
    if (!validation.isValid()) {
      return ResponseEntity.badRequest()
          .body("{\"error\":\"" + String.join(", ", validation.getErrors()) + "\"}");
    }

    // Organize by first letter of filename
    String letter = fileName.substring(0, 1).toLowerCase();
    String artifactPath = "Packages/" + letter + "/" + fileName;
    String tenantId = String.valueOf(repository.getTenantId());
    String repoId = String.valueOf(repository.getId());
    try (InputStream inputStream = file.getInputStream()) {
      blobStore.store(tenantId, repoId, artifactPath, inputStream);
    }

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(summary = "删除RPM包", description = "从hosted类型仓库删除指定的RPM软件包",
      operationId = "yum:deleteRpm")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功"),
      @ApiResponse(responseCode = "403", description = "仓库类型不支持删除"),
      @ApiResponse(responseCode = "404", description = "RPM包不存在")
  })
  @DeleteMapping("/{repositoryName}/Packages/{letter}/{filename}")
  public ResponseEntity<?> deleteRpm(@Parameter(name = "repositoryName", description = "repositoryName") @PathVariable String repositoryName,
      @Parameter(name = "letter", description = "letter") @PathVariable String letter, @Parameter(name = "filename", description = "filename") @PathVariable String filename) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    validateFormat(repository, RepositoryFormat.YUM);
    validateHosted(repository);

    String artifactPath = "Packages/" + letter + "/" + filename;
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
