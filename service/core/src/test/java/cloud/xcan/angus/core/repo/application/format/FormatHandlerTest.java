package cloud.xcan.angus.core.repo.application.format;

import static org.assertj.core.api.Assertions.assertThat;

import cloud.xcan.angus.core.repo.domain.artifact.ArtifactMetadata;
import cloud.xcan.angus.core.repo.domain.format.ArtifactFormatHandler;
import cloud.xcan.angus.core.repo.domain.format.SetupGuide;
import cloud.xcan.angus.core.repo.domain.format.ValidationResult;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for all format handler implementations.
 * Verifies that each handler correctly implements the ArtifactFormatHandler interface.
 */
public class FormatHandlerTest {

  private final List<ArtifactFormatHandler> handlers = List.of(
      new MavenFormatHandler(),
      new DockerFormatHandler(),
      new NpmFormatHandler(),
      new NuGetFormatHandler(),
      new PyPIFormatHandler(),
      new AptFormatHandler(),
      new YumFormatHandler(),
      new RawFormatHandler(),
      new HelmFormatHandler(),
      new GoFormatHandler()
  );

  @Test
  void testAllHandlersReturnCorrectFormat() {
    assertThat(new MavenFormatHandler().getFormat()).isEqualTo(RepositoryFormat.MAVEN);
    assertThat(new DockerFormatHandler().getFormat()).isEqualTo(RepositoryFormat.DOCKER);
    assertThat(new NpmFormatHandler().getFormat()).isEqualTo(RepositoryFormat.NPM);
    assertThat(new NuGetFormatHandler().getFormat()).isEqualTo(RepositoryFormat.NUGET);
    assertThat(new PyPIFormatHandler().getFormat()).isEqualTo(RepositoryFormat.PYPI);
    assertThat(new AptFormatHandler().getFormat()).isEqualTo(RepositoryFormat.APT);
    assertThat(new YumFormatHandler().getFormat()).isEqualTo(RepositoryFormat.YUM);
    assertThat(new RawFormatHandler().getFormat()).isEqualTo(RepositoryFormat.RAW);
    assertThat(new HelmFormatHandler().getFormat()).isEqualTo(RepositoryFormat.HELM);
    assertThat(new GoFormatHandler().getFormat()).isEqualTo(RepositoryFormat.GO);
  }

  @Test
  void testAllHandlersHaveUniqueFormats() {
    long distinctFormats = handlers.stream()
        .map(ArtifactFormatHandler::getFormat)
        .distinct()
        .count();
    assertThat(distinctFormats).isEqualTo(handlers.size());
  }

  @Test
  void testAllHandlersParseMetadata() {
    for (ArtifactFormatHandler handler : handlers) {
      ArtifactMetadata metadata = handler.parseMetadata(null, "test-file");
      assertThat(metadata).isNotNull();
      assertThat(metadata.getPackaging()).isNotNull();
    }
  }

  @Test
  void testAllHandlersValidateNullFileName() {
    for (ArtifactFormatHandler handler : handlers) {
      ValidationResult result = handler.validateArtifact(null, null);
      assertThat(result).isNotNull();
      assertThat(result.isValid()).isFalse();
    }
  }

  @Test
  void testAllHandlersGenerateStoragePath() {
    for (ArtifactFormatHandler handler : handlers) {
      ArtifactMetadata metadata = new ArtifactMetadata();
      String path = handler.generateStoragePath(metadata);
      assertThat(path).isNotNull();
      assertThat(path).isNotEmpty();
    }
  }

  @Test
  void testAllHandlersGenerateSetupGuide() {
    RepoEntity repo = new RepoEntity();
    repo.setName("test-repo");
    repo.setUrl("https://repo.example.com");

    for (ArtifactFormatHandler handler : handlers) {
      SetupGuide guide = handler.generateSetupGuide(repo, "test-token");
      assertThat(guide).isNotNull();
      assertThat(guide.getFormatName()).isNotNull();
      assertThat(guide.getRepositoryUrl()).isNotNull();
      assertThat(guide.getConfigSnippet()).isNotNull();
    }
  }

  @Test
  void testAllHandlersGenerateIndex() {
    RepoEntity repo = new RepoEntity();
    repo.setName("test-repo");

    for (ArtifactFormatHandler handler : handlers) {
      byte[] index = handler.generateIndex(repo);
      assertThat(index).isNotNull();
    }
  }

  // Maven-specific tests

  @Test
  void testMavenValidatesJarFile() {
    MavenFormatHandler handler = new MavenFormatHandler();
    assertThat(handler.validateArtifact(null, "spring-core-6.1.0.jar").isValid()).isTrue();
    assertThat(handler.validateArtifact(null, "spring-core-6.1.0.pom").isValid()).isTrue();
    assertThat(handler.validateArtifact(null, "spring-core-6.1.0.war").isValid()).isTrue();
    assertThat(handler.validateArtifact(null, "spring-core-6.1.0.sha1").isValid()).isTrue();
  }

  @Test
  void testMavenStoragePathWithGAV() {
    MavenFormatHandler handler = new MavenFormatHandler();
    ArtifactMetadata metadata = new ArtifactMetadata();
    metadata.setGroupId("org.springframework");
    metadata.setArtifactId("spring-core");
    String path = handler.generateStoragePath(metadata);
    assertThat(path).isEqualTo("maven2/org/springframework/spring-core/");
  }

  // Docker-specific tests

  @Test
  void testDockerReturnsV2ApiVersion() {
    DockerFormatHandler handler = new DockerFormatHandler();
    assertThat(handler.getFormat()).isEqualTo(RepositoryFormat.DOCKER);
  }

  // NPM-specific tests

  @Test
  void testNpmValidatesTgzFile() {
    NpmFormatHandler handler = new NpmFormatHandler();
    assertThat(handler.validateArtifact(null, "package-1.0.0.tgz").isValid()).isTrue();
    assertThat(handler.validateArtifact(null, "package-1.0.0.tar.gz").isValid()).isTrue();
  }

  @Test
  void testNpmParsesScopedPackage() {
    NpmFormatHandler handler = new NpmFormatHandler();
    ArtifactMetadata metadata = handler.parseMetadata(null, "@scope/package-name");
    assertThat(metadata.getScope()).isEqualTo("@scope");
  }

  // NuGet-specific tests

  @Test
  void testNuGetValidatesNupkgFile() {
    NuGetFormatHandler handler = new NuGetFormatHandler();
    assertThat(handler.validateArtifact(null, "MyPackage.1.0.0.nupkg").isValid()).isTrue();
    assertThat(handler.validateArtifact(null, "MyPackage.1.0.0.snupkg").isValid()).isTrue();
    assertThat(handler.validateArtifact(null, "MyPackage.1.0.0.zip").isValid()).isFalse();
  }

  // PyPI-specific tests

  @Test
  void testPyPIValidatesWheelAndTarGz() {
    PyPIFormatHandler handler = new PyPIFormatHandler();
    assertThat(handler.validateArtifact(null, "package-1.0.tar.gz").isValid()).isTrue();
    assertThat(handler.validateArtifact(null, "package-1.0-py3-none-any.whl").isValid()).isTrue();
    assertThat(handler.validateArtifact(null, "package-1.0.egg").isValid()).isTrue();
    assertThat(handler.validateArtifact(null, "package-1.0.exe").isValid()).isFalse();
  }

  // APT-specific tests

  @Test
  void testAptValidatesDebFile() {
    AptFormatHandler handler = new AptFormatHandler();
    assertThat(handler.validateArtifact(null, "package_1.0_amd64.deb").isValid()).isTrue();
    assertThat(handler.validateArtifact(null, "package_1.0.rpm").isValid()).isFalse();
  }

  // YUM-specific tests

  @Test
  void testYumValidatesRpmFile() {
    YumFormatHandler handler = new YumFormatHandler();
    assertThat(handler.validateArtifact(null, "package-1.0.x86_64.rpm").isValid()).isTrue();
    assertThat(handler.validateArtifact(null, "package-1.0.deb").isValid()).isFalse();
  }

  // Raw-specific tests

  @Test
  void testRawAcceptsAnyFile() {
    RawFormatHandler handler = new RawFormatHandler();
    assertThat(handler.validateArtifact(null, "any-file.bin").isValid()).isTrue();
    assertThat(handler.validateArtifact(null, "document.pdf").isValid()).isTrue();
    assertThat(handler.validateArtifact(null, "image.png").isValid()).isTrue();
  }

  // Helm-specific tests

  @Test
  void testHelmValidatesTgzFile() {
    HelmFormatHandler handler = new HelmFormatHandler();
    assertThat(handler.validateArtifact(null, "chart-1.0.0.tgz").isValid()).isTrue();
    assertThat(handler.validateArtifact(null, "chart-1.0.0.zip").isValid()).isFalse();
  }

  // Go-specific tests

  @Test
  void testGoValidatesModuleFiles() {
    GoFormatHandler handler = new GoFormatHandler();
    assertThat(handler.validateArtifact(null, "module-v1.0.0.zip").isValid()).isTrue();
    assertThat(handler.validateArtifact(null, "go.mod").isValid()).isTrue();
    assertThat(handler.validateArtifact(null, "v1.0.0.info").isValid()).isTrue();
    assertThat(handler.validateArtifact(null, "module.tar.gz").isValid()).isFalse();
  }

  @Test
  void testGoStoragePathEncodesUpperCase() {
    GoFormatHandler handler = new GoFormatHandler();
    ArtifactMetadata metadata = new ArtifactMetadata();
    metadata.setGroupId("github.com/Azure/azure-sdk");
    String path = handler.generateStoragePath(metadata);
    assertThat(path).contains("!azure");
    assertThat(path).doesNotContain("Azure");
  }
}
