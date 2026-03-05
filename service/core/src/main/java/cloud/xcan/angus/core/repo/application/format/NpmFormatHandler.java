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
 * NPM Registry format handler.
 * Implements NPM Registry API compatibility for npm/yarn/pnpm clients.
 * Supports hosted/proxy/group repository types.
 */
@Component
public class NpmFormatHandler implements ArtifactFormatHandler {

  private static final String FORMAT_NAME = "NPM";

  @Override
  public RepositoryFormat getFormat() {
    return RepositoryFormat.NPM;
  }

  @Override
  public ArtifactMetadata parseMetadata(InputStream inputStream, String fileName) {
    ArtifactMetadata metadata = new ArtifactMetadata();
    metadata.setPackaging("npm");
    if (fileName != null && fileName.startsWith("@")) {
      int slashIndex = fileName.indexOf('/');
      if (slashIndex > 0) {
        metadata.setScope(fileName.substring(0, slashIndex));
      }
    }
    return metadata;
  }

  @Override
  public ValidationResult validateArtifact(InputStream inputStream, String fileName) {
    if (fileName == null || fileName.trim().isEmpty()) {
      return ValidationResult.failure("Package name is required");
    }
    if (!fileName.endsWith(".tgz") && !fileName.endsWith(".tar.gz")) {
      ValidationResult result = ValidationResult.success();
      result.addWarning("Non-standard package format: " + fileName);
      return result;
    }
    return ValidationResult.success();
  }

  @Override
  public String generateStoragePath(ArtifactMetadata metadata) {
    StringBuilder path = new StringBuilder("npm/");
    if (metadata.getScope() != null) {
      path.append(metadata.getScope()).append("/");
    }
    if (metadata.getArtifactId() != null) {
      path.append(metadata.getArtifactId()).append("/");
    }
    return path.toString();
  }

  @Override
  public String generateAccessUrl(RepoEntity repository, ArtifactMetadata metadata) {
    String baseUrl = repository.getUrl() != null ? repository.getUrl() : "";
    return baseUrl + "/" + generateStoragePath(metadata);
  }

  @Override
  public ResponseEntity<?> handleFormatSpecificRequest(HttpServletRequest request,
      RepoEntity repository) {
    return ResponseEntity.notFound().build();
  }

  @Override
  public SetupGuide generateSetupGuide(RepoEntity repository, String authToken) {
    String repoUrl = repository.getUrl() != null ? repository.getUrl() : "https://repo.example.com/repository/" + repository.getName();
    String config = String.format(
        "# .npmrc configuration\n"
        + "registry=%s\n"
        + "//%s:_authToken=%s\n\n"
        + "# Or use npm config\n"
        + "npm config set registry %s",
        repoUrl, repoUrl.replaceFirst("https?://", ""),
        authToken != null ? authToken : "{token}", repoUrl);

    SetupGuide guide = new SetupGuide(FORMAT_NAME, repoUrl, config);
    guide.addInstruction("1. Configure .npmrc", "Add the registry URL and auth token to your .npmrc");
    guide.addInstruction("2. Publish packages", "Use 'npm publish' to publish packages");
    guide.addInstruction("3. Install packages", "Use 'npm install' to install packages from this registry");
    return guide;
  }

  @Override
  public byte[] generateIndex(RepoEntity repository) {
    return "{}".getBytes();
  }
}
