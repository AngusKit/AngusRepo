package cloud.xcan.angus.core.repo.domain.format;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cloud.xcan.angus.core.repo.domain.artifact.ArtifactMetadata;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import java.io.InputStream;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class FormatHandlerRegistryTest {

  private FormatHandlerRegistry registry;

  @BeforeEach
  void setUp() {
    ArtifactFormatHandler mavenHandler = new TestFormatHandler(RepositoryFormat.MAVEN);
    ArtifactFormatHandler dockerHandler = new TestFormatHandler(RepositoryFormat.DOCKER);
    registry = new FormatHandlerRegistry(List.of(mavenHandler, dockerHandler));
  }

  @Test
  void testGetHandlerReturnsCorrectHandler() {
    ArtifactFormatHandler handler = registry.getHandler(RepositoryFormat.MAVEN);
    assertThat(handler).isNotNull();
    assertThat(handler.getFormat()).isEqualTo(RepositoryFormat.MAVEN);
  }

  @Test
  void testGetHandlerThrowsForUnsupportedFormat() {
    assertThatThrownBy(() -> registry.getHandler(RepositoryFormat.NPM))
        .isInstanceOf(UnsupportedFormatException.class)
        .hasMessageContaining("Unsupported format: NPM");
  }

  @Test
  void testHasHandler() {
    assertThat(registry.hasHandler(RepositoryFormat.MAVEN)).isTrue();
    assertThat(registry.hasHandler(RepositoryFormat.DOCKER)).isTrue();
    assertThat(registry.hasHandler(RepositoryFormat.NPM)).isFalse();
  }

  @Test
  void testGetAllHandlers() {
    assertThat(registry.getAllHandlers()).hasSize(2);
    assertThat(registry.getAllHandlers()).containsKey(RepositoryFormat.MAVEN);
    assertThat(registry.getAllHandlers()).containsKey(RepositoryFormat.DOCKER);
  }

  // Simple test handler implementation
  private static class TestFormatHandler implements ArtifactFormatHandler {
    private final RepositoryFormat format;

    TestFormatHandler(RepositoryFormat format) {
      this.format = format;
    }

    @Override
    public RepositoryFormat getFormat() {
      return format;
    }

    @Override
    public ArtifactMetadata parseMetadata(InputStream inputStream, String fileName) {
      return new ArtifactMetadata();
    }

    @Override
    public ValidationResult validateArtifact(InputStream inputStream, String fileName) {
      return ValidationResult.success();
    }

    @Override
    public String generateStoragePath(ArtifactMetadata metadata) {
      return format.getValue() + "/";
    }

    @Override
    public String generateAccessUrl(RepoEntity repository, ArtifactMetadata metadata) {
      return "";
    }

    @Override
    public ResponseEntity<?> handleFormatSpecificRequest(HttpServletRequest request,
        RepoEntity repository) {
      return ResponseEntity.ok().build();
    }

    @Override
    public SetupGuide generateSetupGuide(RepoEntity repository, String authToken) {
      return new SetupGuide();
    }

    @Override
    public byte[] generateIndex(RepoEntity repository) {
      return new byte[0];
    }
  }
}
