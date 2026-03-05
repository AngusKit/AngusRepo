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
 * YUM (RPM) repository format handler.
 * Implements YUM/DNF repository protocol compatibility.
 * Supports hosted/proxy/group repository types for yum/dnf commands.
 */
@Component
public class YumFormatHandler implements ArtifactFormatHandler {

  private static final String FORMAT_NAME = "YUM";

  @Override
  public RepositoryFormat getFormat() {
    return RepositoryFormat.YUM;
  }

  @Override
  public ArtifactMetadata parseMetadata(InputStream inputStream, String fileName) {
    ArtifactMetadata metadata = new ArtifactMetadata();
    metadata.setPackaging("rpm");
    // NEVRA would be parsed from RPM header
    return metadata;
  }

  @Override
  public ValidationResult validateArtifact(InputStream inputStream, String fileName) {
    if (fileName == null || fileName.trim().isEmpty()) {
      return ValidationResult.failure("Package file name is required");
    }
    if (!fileName.toLowerCase().endsWith(".rpm")) {
      return ValidationResult.failure("Invalid YUM package format. Expected .rpm file");
    }
    return ValidationResult.success();
  }

  @Override
  public String generateStoragePath(ArtifactMetadata metadata) {
    StringBuilder path = new StringBuilder("yum/Packages/");
    if (metadata.getArtifactId() != null) {
      String name = metadata.getArtifactId();
      path.append(name.substring(0, 1).toUpperCase()).append("/");
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
    if (path.endsWith("/repodata/repomd.xml")) {
      return ResponseEntity.ok().header("Content-Type", "application/xml").build();
    }
    return ResponseEntity.notFound().build();
  }

  @Override
  public SetupGuide generateSetupGuide(RepoEntity repository, String authToken) {
    String repoUrl = repository.getUrl() != null ? repository.getUrl() : "https://repo.example.com/repository/" + repository.getName();
    String config = String.format(
        "# Create YUM repository config\n"
        + "cat > /etc/yum.repos.d/%s.repo << EOF\n"
        + "[%s]\n"
        + "name=%s\n"
        + "baseurl=%s\n"
        + "enabled=1\n"
        + "gpgcheck=0\n"
        + "EOF\n\n"
        + "# Install packages\n"
        + "yum install package-name",
        repository.getName(), repository.getName(),
        repository.getName(), repoUrl);

    SetupGuide guide = new SetupGuide(FORMAT_NAME, repoUrl, config);
    guide.addInstruction("1. Create repo config", "Create a .repo file in /etc/yum.repos.d/");
    guide.addInstruction("2. Install packages", "Use 'yum install' or 'dnf install' to install packages");
    return guide;
  }

  @Override
  public byte[] generateIndex(RepoEntity repository) {
    // repomd.xml skeleton
    String repomd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<repomd xmlns=\"http://linux.duke.edu/metadata/repo\">\n"
        + "  <revision>" + System.currentTimeMillis() + "</revision>\n"
        + "</repomd>";
    return repomd.getBytes();
  }
}
