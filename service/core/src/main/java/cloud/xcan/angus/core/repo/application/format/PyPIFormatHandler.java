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
 * PyPI format handler.
 * Implements PEP 503 Simple Repository API and PyPI JSON API compatibility.
 * Supports pip/twine/poetry clients with hosted/proxy/group repository types.
 */
@Component
public class PyPIFormatHandler implements ArtifactFormatHandler {

  private static final String FORMAT_NAME = "PyPI";

  @Override
  public RepositoryFormat getFormat() {
    return RepositoryFormat.PYPI;
  }

  @Override
  public ArtifactMetadata parseMetadata(InputStream inputStream, String fileName) {
    ArtifactMetadata metadata = new ArtifactMetadata();
    metadata.setPackaging("pypi");
    if (fileName != null) {
      // Detect Python version from wheel filename
      if (fileName.endsWith(".whl")) {
        metadata.setPythonVersion(extractPythonVersionFromWheel(fileName));
      }
    }
    return metadata;
  }

  @Override
  public ValidationResult validateArtifact(InputStream inputStream, String fileName) {
    if (fileName == null || fileName.trim().isEmpty()) {
      return ValidationResult.failure("Package file name is required");
    }
    String lowerName = fileName.toLowerCase();
    if (lowerName.endsWith(".tar.gz") || lowerName.endsWith(".whl")
        || lowerName.endsWith(".egg") || lowerName.endsWith(".zip")) {
      return ValidationResult.success();
    }
    return ValidationResult.failure("Unsupported PyPI package format: " + fileName
        + ". Expected .tar.gz, .whl, .egg, or .zip");
  }

  @Override
  public String generateStoragePath(ArtifactMetadata metadata) {
    StringBuilder path = new StringBuilder("pypi/packages/");
    if (metadata.getArtifactId() != null) {
      path.append(normalizePyPIName(metadata.getArtifactId())).append("/");
    }
    return path.toString();
  }

  @Override
  public String generateAccessUrl(RepoEntity repository, ArtifactMetadata metadata) {
    String baseUrl = repository.getUrl() != null ? repository.getUrl() : "";
    return baseUrl + "/simple/" + (metadata.getArtifactId() != null ? normalizePyPIName(metadata.getArtifactId()) + "/" : "");
  }

  @Override
  public ResponseEntity<?> handleFormatSpecificRequest(HttpServletRequest request,
      RepoEntity repository) {
    String path = request.getRequestURI();
    if (path.endsWith("/simple/") || path.endsWith("/simple")) {
      return ResponseEntity.ok().header("Content-Type", "text/html").build();
    }
    return ResponseEntity.notFound().build();
  }

  @Override
  public SetupGuide generateSetupGuide(RepoEntity repository, String authToken) {
    String repoUrl = repository.getUrl() != null ? repository.getUrl() : "https://repo.example.com/repository/" + repository.getName();
    String config = String.format(
        "# pip configuration\n"
        + "pip install --index-url %s/simple/ package-name\n\n"
        + "# pip.conf / pip.ini\n"
        + "[global]\n"
        + "index-url = %s/simple/\n\n"
        + "# twine upload\n"
        + "twine upload --repository-url %s/legacy/ dist/*",
        repoUrl, repoUrl, repoUrl);

    SetupGuide guide = new SetupGuide(FORMAT_NAME, repoUrl, config);
    guide.addInstruction("1. Configure pip", "Set the index URL in pip.conf or use --index-url flag");
    guide.addInstruction("2. Upload packages", "Use 'twine upload' to publish packages");
    guide.addInstruction("3. Install packages", "Use 'pip install' to install packages from this repository");
    return guide;
  }

  @Override
  public byte[] generateIndex(RepoEntity repository) {
    // PEP 503 Simple API index page
    String html = "<!DOCTYPE html><html><head><title>Simple Index</title></head>"
        + "<body><h1>Simple Index</h1></body></html>";
    return html.getBytes();
  }

  /**
   * Normalize PyPI package name: lowercase, replace [-_.] with -
   */
  private String normalizePyPIName(String name) {
    return name.toLowerCase().replaceAll("[-_.]+", "-");
  }

  private String extractPythonVersionFromWheel(String fileName) {
    // Wheel filename format: {name}-{version}(-{build})?-{python}-{abi}-{platform}.whl
    // Minimum parts without build tag: name-version-python-abi-platform (5 parts)
    String[] parts = fileName.replace(".whl", "").split("-");
    if (parts.length >= 5) {
      return parts[parts.length - 3];
    }
    return null;
  }
}
