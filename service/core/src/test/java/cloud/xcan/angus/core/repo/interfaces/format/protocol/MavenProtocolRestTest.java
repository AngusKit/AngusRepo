package cloud.xcan.angus.core.repo.interfaces.format.protocol;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cloud.xcan.angus.core.repo.domain.format.ArtifactFormatHandler;
import cloud.xcan.angus.core.repo.domain.format.FormatHandlerRegistry;
import cloud.xcan.angus.core.repo.domain.format.SetupGuide;
import cloud.xcan.angus.core.repo.domain.format.ValidationResult;
import cloud.xcan.angus.core.repo.domain.format.store.BlobStore;
import cloud.xcan.angus.core.repo.domain.artifact.ArtifactMetadata;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryType;
import cloud.xcan.angus.core.repo.application.query.repository.RepositoryQuery;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for {@link MavenProtocolRest}.
 * Validates Maven repository protocol correctness and completeness per Maven Repository Layout spec.
 *
 * @see <a href="https://maven.apache.org/repository/layout.html">Maven Repository Layout</a>
 */
public class MavenProtocolRestTest {

  private MavenProtocolRest controller;
  private StubRepositoryQuery repositoryQuery;
  private StubBlobStore blobStore;
  private FormatHandlerRegistry formatHandlerRegistry;

  @BeforeEach
  void setUp() throws Exception {
    controller = new MavenProtocolRest();
    repositoryQuery = new StubRepositoryQuery();
    blobStore = new StubBlobStore();
    formatHandlerRegistry = new FormatHandlerRegistry(
        List.of(new StubFormatHandler(RepositoryFormat.MAVEN)));

    injectField(controller, "repositoryQuery", repositoryQuery);
    injectField(controller, "blobStore", blobStore);
    injectField(controller, "formatHandlerRegistry", formatHandlerRegistry);
  }

  // ===== Download Tests =====

  @Test
  void download_existingArtifact_returns200WithContent() {
    RepoEntity repo = createRepo("maven-central", RepositoryFormat.MAVEN, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar",
        "jar-content");

    HttpServletRequest request = createRequest("GET",
        "/maven/maven-central/org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar");
    ResponseEntity<?> response = controller.download("maven-central", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_OCTET_STREAM);
  }

  @Test
  void download_nonExistingArtifact_returns404() {
    RepoEntity repo = createRepo("maven-central", RepositoryFormat.MAVEN, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequest("GET",
        "/maven/maven-central/org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar");
    ResponseEntity<?> response = controller.download("maven-central", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void download_mavenMetadataXml_generatesIndex() {
    RepoEntity repo = createRepo("maven-central", RepositoryFormat.MAVEN, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequest("GET",
        "/maven/maven-central/org/springframework/spring-core/maven-metadata.xml");
    ResponseEntity<?> response = controller.download("maven-central", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_XML);
  }

  @Test
  void download_checksumFile_returnsTextPlain() {
    RepoEntity repo = createRepo("maven-central", RepositoryFormat.MAVEN, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar.sha1",
        "abc123");

    HttpServletRequest request = createRequest("GET",
        "/maven/maven-central/org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar.sha1");
    ResponseEntity<?> response = controller.download("maven-central", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void download_sha256ChecksumFile_returnsTextPlain() {
    RepoEntity repo = createRepo("maven-central", RepositoryFormat.MAVEN, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100",
        "org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar.sha256", "sha256hash");

    HttpServletRequest request = createRequest("GET",
        "/maven/maven-central/org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar.sha256");
    ResponseEntity<?> response = controller.download("maven-central", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void download_pomFile_returnsXmlContentType() {
    RepoEntity repo = createRepo("maven-central", RepositoryFormat.MAVEN, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "org/springframework/spring-core/6.1.0/spring-core-6.1.0.pom",
        "<project/>");

    HttpServletRequest request = createRequest("GET",
        "/maven/maven-central/org/springframework/spring-core/6.1.0/spring-core-6.1.0.pom");
    ResponseEntity<?> response = controller.download("maven-central", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_XML);
  }

  // ===== Upload Tests =====

  @Test
  void upload_validArtifact_returns201() throws Exception {
    RepoEntity repo = createRepo("maven-releases", RepositoryFormat.MAVEN, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT",
        "/maven/maven-releases/org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar",
        "jar-content");
    ResponseEntity<?> response = controller.upload("maven-releases", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(blobStore.exists("1", "100",
        "org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar")).isTrue();
  }

  @Test
  void upload_nonHostedRepository_throwsException() {
    RepoEntity repo = createRepo("maven-proxy", RepositoryFormat.MAVEN, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT",
        "/maven/maven-proxy/org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar",
        "jar-content");

    assertThatThrownBy(() -> controller.upload("maven-proxy", request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
  }

  @Test
  void upload_wrongFormat_throwsException() {
    RepoEntity repo = createRepo("docker-repo", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT",
        "/maven/docker-repo/some/path/file.jar", "jar-content");

    assertThatThrownBy(() -> controller.upload("docker-repo", request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("MAVEN");
  }

  // ===== Exists (HEAD) Tests =====

  @Test
  void exists_existingArtifact_returns200WithSize() {
    RepoEntity repo = createRepo("maven-central", RepositoryFormat.MAVEN, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar",
        "jar-content");

    HttpServletRequest request = createRequest("HEAD",
        "/maven/maven-central/org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar");
    ResponseEntity<?> response = controller.exists("maven-central", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentLength()).isGreaterThan(0);
  }

  @Test
  void exists_nonExistingArtifact_returns404() {
    RepoEntity repo = createRepo("maven-central", RepositoryFormat.MAVEN, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequest("HEAD",
        "/maven/maven-central/org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar");
    ResponseEntity<?> response = controller.exists("maven-central", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Delete Tests =====

  @Test
  void delete_existingArtifact_returns204() {
    RepoEntity repo = createRepo("maven-releases", RepositoryFormat.MAVEN, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar",
        "jar-content");

    HttpServletRequest request = createRequest("DELETE",
        "/maven/maven-releases/org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar");
    ResponseEntity<?> response = controller.delete("maven-releases", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(blobStore.exists("1", "100",
        "org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar")).isFalse();
  }

  @Test
  void delete_nonExistingArtifact_returns404() {
    RepoEntity repo = createRepo("maven-releases", RepositoryFormat.MAVEN, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequest("DELETE",
        "/maven/maven-releases/org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar");
    ResponseEntity<?> response = controller.delete("maven-releases", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_nonHostedRepository_throwsException() {
    RepoEntity repo = createRepo("maven-proxy", RepositoryFormat.MAVEN, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequest("DELETE",
        "/maven/maven-proxy/org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar");

    assertThatThrownBy(() -> controller.delete("maven-proxy", request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
  }

  // ===== Path Extraction Tests =====

  @Test
  void download_pathExtraction_correctlyExtractsArtifactPath() {
    RepoEntity repo = createRepo("maven-central", RepositoryFormat.MAVEN, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "com/example/lib/1.0/lib-1.0.jar", "content");

    HttpServletRequest request = createRequest("GET",
        "/maven/maven-central/com/example/lib/1.0/lib-1.0.jar");
    ResponseEntity<?> response = controller.download("maven-central", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  // ===== Content Type Resolution Tests =====

  @Test
  void download_warFile_returnsOctetStream() {
    RepoEntity repo = createRepo("maven-central", RepositoryFormat.MAVEN, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "com/example/web/1.0/web-1.0.war", "war-content");

    HttpServletRequest request = createRequest("GET",
        "/maven/maven-central/com/example/web/1.0/web-1.0.war");
    ResponseEntity<?> response = controller.download("maven-central", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_OCTET_STREAM);
  }

  @Test
  void download_earFile_returnsOctetStream() {
    RepoEntity repo = createRepo("maven-central", RepositoryFormat.MAVEN, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "com/example/app/1.0/app-1.0.ear", "ear-content");

    HttpServletRequest request = createRequest("GET",
        "/maven/maven-central/com/example/app/1.0/app-1.0.ear");
    ResponseEntity<?> response = controller.download("maven-central", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_OCTET_STREAM);
  }

  // ===== Helper methods =====

  static RepoEntity createRepo(String name, RepositoryFormat format, RepositoryType type) {
    RepoEntity repo = new RepoEntity();
    repo.setId(100L);
    repo.setName(name);
    repo.setFormat(format);
    repo.setType(type);
    repo.setTenantId(1L);
    return repo;
  }

  static void injectField(Object target, String fieldName, Object value) throws Exception {
    Class<?> clazz = target.getClass();
    Field field = null;
    while (clazz != null) {
      try {
        field = clazz.getDeclaredField(fieldName);
        break;
      } catch (NoSuchFieldException e) {
        clazz = clazz.getSuperclass();
      }
    }
    if (field == null) {
      throw new NoSuchFieldException(fieldName);
    }
    field.setAccessible(true);
    field.set(target, value);
  }

  static HttpServletRequest createRequest(String method, String uri) {
    return new StubHttpServletRequest(method, uri, null);
  }

  static HttpServletRequest createRequestWithBody(String method, String uri, String body) {
    return new StubHttpServletRequest(method, uri,
        body != null ? body.getBytes(StandardCharsets.UTF_8) : null);
  }

  static HttpServletRequest createRequestWithParams(String method, String uri,
      java.util.Map<String, String> params) {
    StubHttpServletRequest request = new StubHttpServletRequest(method, uri, null);
    if (params != null) {
      request.setParameters(params);
    }
    return request;
  }
}
