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
 * Go Module Proxy format handler.
 * Implements Go Module Proxy Protocol (GOPROXY) compatibility.
 * Supports hosted/proxy/group repository types for go commands.
 */
@Component
public class GoFormatHandler implements ArtifactFormatHandler {

  private static final String FORMAT_NAME = "Go";

  @Override
  public RepositoryFormat getFormat() {
    return RepositoryFormat.GO;
  }

  @Override
  public ArtifactMetadata parseMetadata(InputStream inputStream, String fileName) {
    ArtifactMetadata metadata = new ArtifactMetadata();
    metadata.setPackaging("go");
    return metadata;
  }

  @Override
  public ValidationResult validateArtifact(InputStream inputStream, String fileName) {
    if (fileName == null || fileName.trim().isEmpty()) {
      return ValidationResult.failure("Module file name is required");
    }
    String lowerName = fileName.toLowerCase();
    if (lowerName.endsWith(".zip") || lowerName.endsWith(".mod")
        || lowerName.endsWith(".info")) {
      return ValidationResult.success();
    }
    return ValidationResult.failure("Invalid Go module format: " + fileName
        + ". Expected .zip, .mod, or .info file");
  }

  @Override
  public String generateStoragePath(ArtifactMetadata metadata) {
    StringBuilder path = new StringBuilder("go/");
    if (metadata.getGroupId() != null) {
      // Module path encoding: uppercase letters → !lowercase
      path.append(encodeModulePath(metadata.getGroupId())).append("/");
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
        "# Set GOPROXY environment variable\n"
        + "export GOPROXY=%s,direct\n\n"
        + "# For private modules, set GONOSUMCHECK\n"
        + "export GONOSUMCHECK=your.private.module/*\n\n"
        + "# Configure authentication via .netrc\n"
        + "echo \"machine %s login {username} password %s\" >> ~/.netrc",
        repoUrl, repoUrl.replaceFirst("https?://", "").split("/")[0],
        authToken != null ? authToken : "{token}");

    SetupGuide guide = new SetupGuide(FORMAT_NAME, repoUrl, config);
    guide.addInstruction("1. Set GOPROXY", "Configure the GOPROXY environment variable to use this repository");
    guide.addInstruction("2. Configure auth", "Add credentials to ~/.netrc for authentication");
    guide.addInstruction("3. Use go commands", "Use 'go get' and 'go mod download' normally");
    return guide;
  }

  @Override
  public byte[] generateIndex(RepoEntity repository) {
    // Go module proxy doesn't have a standard index file
    return new byte[0];
  }

  /**
   * Encode Go module path: uppercase letters are converted to !lowercase.
   * e.g., github.com/Azure/azure-sdk → github.com/!azure/azure-sdk
   */
  private String encodeModulePath(String modulePath) {
    StringBuilder encoded = new StringBuilder();
    for (char c : modulePath.toCharArray()) {
      if (Character.isUpperCase(c)) {
        encoded.append('!').append(Character.toLowerCase(c));
      } else {
        encoded.append(c);
      }
    }
    return encoded.toString();
  }
}
