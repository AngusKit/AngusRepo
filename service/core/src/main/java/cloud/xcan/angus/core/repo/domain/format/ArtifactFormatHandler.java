package cloud.xcan.angus.core.repo.domain.format;

import cloud.xcan.angus.core.repo.domain.artifact.ArtifactMetadata;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import java.io.InputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

/**
 * Artifact format handler interface - top-level abstraction for all format services.
 * All repository format services share this unified abstract interface, 
 * implementing format-specific processing through the Strategy Pattern.
 */
public interface ArtifactFormatHandler {

  /**
   * Get the supported format.
   */
  RepositoryFormat getFormat();

  /**
   * Parse artifact metadata from an input stream.
   */
  ArtifactMetadata parseMetadata(InputStream inputStream, String fileName);

  /**
   * Validate artifact format validity.
   */
  ValidationResult validateArtifact(InputStream inputStream, String fileName);

  /**
   * Generate artifact storage path.
   */
  String generateStoragePath(ArtifactMetadata metadata);

  /**
   * Generate artifact access URL.
   */
  String generateAccessUrl(RepoEntity repository, ArtifactMetadata metadata);

  /**
   * Handle format-specific API requests (e.g., Maven metadata.xml, Docker manifest, etc.).
   */
  ResponseEntity<?> handleFormatSpecificRequest(HttpServletRequest request, RepoEntity repository);

  /**
   * Generate client configuration setup guide.
   */
  SetupGuide generateSetupGuide(RepoEntity repository, String authToken);

  /**
   * Get format-specific index/metadata files.
   */
  byte[] generateIndex(RepoEntity repository);
}
