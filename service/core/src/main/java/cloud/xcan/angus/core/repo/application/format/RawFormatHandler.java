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
 * Raw repository format handler.
 * Provides simple HTTP file storage and download for arbitrary file types.
 * Supports hosted/proxy/group repository types with no specific format requirements.
 */
@Component
public class RawFormatHandler implements ArtifactFormatHandler {

  private static final String FORMAT_NAME = "Raw";

  @Override
  public RepositoryFormat getFormat() {
    return RepositoryFormat.RAW;
  }

  @Override
  public ArtifactMetadata parseMetadata(InputStream inputStream, String fileName) {
    ArtifactMetadata metadata = new ArtifactMetadata();
    metadata.setPackaging("raw");
    return metadata;
  }

  @Override
  public ValidationResult validateArtifact(InputStream inputStream, String fileName) {
    if (fileName == null || fileName.trim().isEmpty()) {
      return ValidationResult.failure("File name is required");
    }
    // Raw format accepts any file type
    return ValidationResult.success();
  }

  @Override
  public String generateStoragePath(ArtifactMetadata metadata) {
    return "raw/";
  }

  @Override
  public String generateAccessUrl(RepoEntity repository, ArtifactMetadata metadata) {
    String baseUrl = repository.getUrl() != null ? repository.getUrl() : "";
    return baseUrl + "/" + generateStoragePath(metadata);
  }

  @Override
  public ResponseEntity<?> handleFormatSpecificRequest(HttpServletRequest request,
      RepoEntity repository) {
    // Raw format has no format-specific API endpoints
    return ResponseEntity.notFound().build();
  }

  @Override
  public SetupGuide generateSetupGuide(RepoEntity repository, String authToken) {
    String repoUrl = repository.getUrl() != null ? repository.getUrl() : "https://repo.example.com/repository/" + repository.getName();
    String config = String.format(
        "# Upload file\n"
        + "curl -u {username}:%s -T my-file.bin %s/path/to/my-file.bin\n\n"
        + "# Download file\n"
        + "curl -O %s/path/to/my-file.bin",
        authToken != null ? authToken : "{token}", repoUrl, repoUrl);

    SetupGuide guide = new SetupGuide(FORMAT_NAME, repoUrl, config);
    guide.addInstruction("1. Upload files", "Use HTTP PUT to upload files to any path");
    guide.addInstruction("2. Download files", "Use HTTP GET to download files");
    return guide;
  }

  @Override
  public byte[] generateIndex(RepoEntity repository) {
    // Raw repositories don't have a standard index format
    return new byte[0];
  }
}
