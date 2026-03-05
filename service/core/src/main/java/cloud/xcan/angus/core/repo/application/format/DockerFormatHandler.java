package cloud.xcan.angus.core.repo.application.format;

import cloud.xcan.angus.core.repo.domain.artifact.ArtifactMetadata;
import cloud.xcan.angus.core.repo.domain.format.ArtifactFormatHandler;
import cloud.xcan.angus.core.repo.domain.format.SetupGuide;
import cloud.xcan.angus.core.repo.domain.format.ValidationResult;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import java.io.InputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Docker Registry format handler.
 * Implements Docker Registry HTTP API V2 protocol compatibility.
 * Supports hosted/proxy/group repository types for docker push/pull/login.
 */
@Component
public class DockerFormatHandler implements ArtifactFormatHandler {

  private static final String FORMAT_NAME = "Docker";
  private static final String API_VERSION_HEADER = "Docker-Distribution-Api-Version";
  private static final String API_VERSION_VALUE = "registry/2.0";

  @Override
  public RepositoryFormat getFormat() {
    return RepositoryFormat.DOCKER;
  }

  @Override
  public ArtifactMetadata parseMetadata(InputStream inputStream, String fileName) {
    ArtifactMetadata metadata = new ArtifactMetadata();
    metadata.setPackaging("docker");
    // Docker metadata is parsed from the manifest JSON
    return metadata;
  }

  @Override
  public ValidationResult validateArtifact(InputStream inputStream, String fileName) {
    if (fileName == null || fileName.trim().isEmpty()) {
      return ValidationResult.failure("Image reference is required");
    }
    return ValidationResult.success();
  }

  @Override
  public String generateStoragePath(ArtifactMetadata metadata) {
    StringBuilder path = new StringBuilder("docker/");
    if (metadata.getDigest() != null) {
      path.append("blobs/").append(metadata.getDigest());
    }
    return path.toString();
  }

  @Override
  public String generateAccessUrl(RepoEntity repository, ArtifactMetadata metadata) {
    String baseUrl = repository.getUrl() != null ? repository.getUrl() : "";
    return baseUrl + "/v2/" + generateStoragePath(metadata);
  }

  @Override
  public ResponseEntity<?> handleFormatSpecificRequest(HttpServletRequest request,
      RepoEntity repository) {
    String path = request.getRequestURI();
    // Docker Registry V2 API version check
    if (path.endsWith("/v2/") || path.endsWith("/v2")) {
      return ResponseEntity.ok()
          .header(API_VERSION_HEADER, API_VERSION_VALUE)
          .build();
    }
    return ResponseEntity.notFound().build();
  }

  @Override
  public SetupGuide generateSetupGuide(RepoEntity repository, String authToken) {
    String repoUrl = repository.getUrl() != null ? repository.getUrl() : "repo.example.com";
    String config = String.format(
        "# Docker Login\n"
        + "docker login %s\n\n"
        + "# Tag and Push\n"
        + "docker tag my-image:latest %s/my-image:latest\n"
        + "docker push %s/my-image:latest\n\n"
        + "# Pull\n"
        + "docker pull %s/my-image:latest",
        repoUrl, repoUrl, repoUrl, repoUrl);

    SetupGuide guide = new SetupGuide(FORMAT_NAME, repoUrl, config);
    guide.addInstruction("1. Login", "Run 'docker login " + repoUrl + "' and enter your credentials");
    guide.addInstruction("2. Push images", "Tag your image and push to the repository");
    guide.addInstruction("3. Pull images", "Pull images directly from the repository");
    return guide;
  }

  @Override
  public byte[] generateIndex(RepoEntity repository) {
    // Docker uses _catalog endpoint for listing, not a file-based index
    String catalog = "{\"repositories\":[]}";
    return catalog.getBytes();
  }
}
