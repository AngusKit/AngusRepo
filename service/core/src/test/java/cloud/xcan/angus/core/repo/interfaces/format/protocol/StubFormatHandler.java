package cloud.xcan.angus.core.repo.interfaces.format.protocol;

import cloud.xcan.angus.core.repo.domain.artifact.ArtifactMetadata;
import cloud.xcan.angus.core.repo.domain.format.ArtifactFormatHandler;
import cloud.xcan.angus.core.repo.domain.format.SetupGuide;
import cloud.xcan.angus.core.repo.domain.format.ValidationResult;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import jakarta.servlet.http.HttpServletRequest;
import java.io.InputStream;
import org.springframework.http.ResponseEntity;

/**
 * Stub implementation of {@link ArtifactFormatHandler} for unit testing protocol controllers.
 * Always returns success validation and simple default values.
 */
class StubFormatHandler implements ArtifactFormatHandler {

  private final RepositoryFormat format;

  StubFormatHandler(RepositoryFormat format) {
    this.format = format;
  }

  @Override
  public RepositoryFormat getFormat() {
    return format;
  }

  @Override
  public ArtifactMetadata parseMetadata(InputStream inputStream, String fileName) {
    ArtifactMetadata metadata = new ArtifactMetadata();
    metadata.setPackaging(format.getValue());
    return metadata;
  }

  @Override
  public ValidationResult validateArtifact(InputStream inputStream, String fileName) {
    if (fileName == null || fileName.isBlank()) {
      return ValidationResult.failure("File name is required");
    }
    return ValidationResult.success();
  }

  @Override
  public String generateStoragePath(ArtifactMetadata metadata) {
    return format.getValue() + "/";
  }

  @Override
  public String generateAccessUrl(RepoEntity repository, ArtifactMetadata metadata) {
    return "/" + format.getValue() + "/" + repository.getName();
  }

  @Override
  public ResponseEntity<?> handleFormatSpecificRequest(HttpServletRequest request,
      RepoEntity repository) {
    return ResponseEntity.ok().build();
  }

  @Override
  public SetupGuide generateSetupGuide(RepoEntity repository, String authToken) {
    return new SetupGuide(format.getValue(), repository.getUrl(), "# config");
  }

  @Override
  public byte[] generateIndex(RepoEntity repository) {
    return ("<index>" + format.getValue() + "</index>").getBytes();
  }
}
