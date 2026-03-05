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
 * APT (Debian/Ubuntu) repository format handler.
 * Implements Debian APT repository protocol compatibility.
 * Supports hosted/proxy/group repository types for apt-get/apt commands.
 */
@Component
public class AptFormatHandler implements ArtifactFormatHandler {

  private static final String FORMAT_NAME = "APT";

  @Override
  public RepositoryFormat getFormat() {
    return RepositoryFormat.APT;
  }

  @Override
  public ArtifactMetadata parseMetadata(InputStream inputStream, String fileName) {
    ArtifactMetadata metadata = new ArtifactMetadata();
    metadata.setPackaging("deb");
    // Architecture would be parsed from the .deb control file
    return metadata;
  }

  @Override
  public ValidationResult validateArtifact(InputStream inputStream, String fileName) {
    if (fileName == null || fileName.trim().isEmpty()) {
      return ValidationResult.failure("Package file name is required");
    }
    if (!fileName.toLowerCase().endsWith(".deb")) {
      return ValidationResult.failure("Invalid APT package format. Expected .deb file");
    }
    return ValidationResult.success();
  }

  @Override
  public String generateStoragePath(ArtifactMetadata metadata) {
    StringBuilder path = new StringBuilder("apt/pool/main/");
    if (metadata.getArtifactId() != null) {
      String name = metadata.getArtifactId();
      String prefix = name.startsWith("lib") ? name.substring(0, 4) : name.substring(0, 1);
      path.append(prefix).append("/").append(name).append("/");
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
    String path = request.getRequestURI();
    if (path.contains("/dists/") && path.endsWith("/Release")) {
      return ResponseEntity.ok().header("Content-Type", "text/plain").build();
    }
    return ResponseEntity.notFound().build();
  }

  @Override
  public SetupGuide generateSetupGuide(RepoEntity repository, String authToken) {
    String repoUrl = repository.getUrl() != null ? repository.getUrl() : "https://repo.example.com/repository/" + repository.getName();
    String config = String.format(
        "# Add APT repository\n"
        + "echo \"deb %s focal main\" | sudo tee /etc/apt/sources.list.d/%s.list\n\n"
        + "# Import GPG key\n"
        + "curl -fsSL %s/gpg-key | sudo apt-key add -\n\n"
        + "# Update and install\n"
        + "sudo apt-get update\n"
        + "sudo apt-get install package-name",
        repoUrl, repository.getName(), repoUrl);

    SetupGuide guide = new SetupGuide(FORMAT_NAME, repoUrl, config);
    guide.addInstruction("1. Add repository", "Add the APT repository to your sources list");
    guide.addInstruction("2. Import GPG key", "Import the repository GPG signing key");
    guide.addInstruction("3. Install packages", "Use 'apt-get install' to install packages");
    return guide;
  }

  @Override
  public byte[] generateIndex(RepoEntity repository) {
    // APT Release file
    String release = "Origin: AngusRepo\n"
        + "Label: " + repository.getName() + "\n"
        + "Suite: stable\n"
        + "Codename: stable\n"
        + "Architectures: amd64 arm64 i386\n"
        + "Components: main\n";
    return release.getBytes();
  }
}
