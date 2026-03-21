package cloud.xcan.angus.core.repo.interfaces.format.protocol;

import static cloud.xcan.angus.core.repo.interfaces.format.protocol.MavenProtocolRestTest.createRepo;
import static cloud.xcan.angus.core.repo.interfaces.format.protocol.MavenProtocolRestTest.createRequestWithBody;
import static cloud.xcan.angus.core.repo.interfaces.format.protocol.MavenProtocolRestTest.injectField;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryType;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for {@link NpmProtocolRest}.
 * Validates NPM registry protocol correctness including package documents, tarballs, and dist-tags.
 */
public class NpmProtocolRestTest {

  private NpmProtocolRest controller;
  private StubRepositoryQuery repositoryQuery;
  private StubBlobStore blobStore;

  @BeforeEach
  void setUp() throws Exception {
    controller = new NpmProtocolRest();
    repositoryQuery = new StubRepositoryQuery();
    blobStore = new StubBlobStore();

    injectField(controller, "repositoryQuery", repositoryQuery);
    injectField(controller, "blobStore", blobStore);
  }

  // ===== Get Package Document Tests =====

  @Test
  void getPackageDocument_existing_returns200() {
    RepoEntity repo = createRepo("npm-hosted", RepositoryFormat.NPM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "lodash/package.json", "{\"name\":\"lodash\"}");

    ResponseEntity<?> response = controller.getPackageDocument("npm-hosted", "lodash");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void getPackageDocument_notFound_returns404() {
    RepoEntity repo = createRepo("npm-hosted", RepositoryFormat.NPM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.getPackageDocument("npm-hosted", "missing-pkg");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void getPackageDocument_wrongFormat_throwsException() {
    RepoEntity repo = createRepo("docker-repo", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    assertThatThrownBy(() -> controller.getPackageDocument("docker-repo", "lodash"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("npm");
  }

  // ===== Get Package Version Tests =====

  @Test
  void getPackageVersion_existing_returns200() {
    RepoEntity repo = createRepo("npm-hosted", RepositoryFormat.NPM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "lodash/4.17.21/package.json", "{\"version\":\"4.17.21\"}");

    ResponseEntity<?> response = controller.getPackageVersion("npm-hosted", "lodash", "4.17.21");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void getPackageVersion_notFound_returns404() {
    RepoEntity repo = createRepo("npm-hosted", RepositoryFormat.NPM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.getPackageVersion("npm-hosted", "lodash", "99.0.0");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Download Tarball Tests =====

  @Test
  void downloadTarball_existing_returns200() {
    RepoEntity repo = createRepo("npm-hosted", RepositoryFormat.NPM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "lodash/-/lodash-4.17.21.tgz", "tarball-content");

    ResponseEntity<?> response = controller.downloadTarball("npm-hosted", "lodash",
        "lodash-4.17.21.tgz");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void downloadTarball_notFound_returns404() {
    RepoEntity repo = createRepo("npm-hosted", RepositoryFormat.NPM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.downloadTarball("npm-hosted", "lodash",
        "lodash-99.0.0.tgz");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Publish Tests =====

  @Test
  void publish_success_returns201() throws Exception {
    RepoEntity repo = createRepo("npm-hosted", RepositoryFormat.NPM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT",
        "/npm/npm-hosted/lodash", "{\"name\":\"lodash\"}");
    ResponseEntity<?> response = controller.publish("npm-hosted", "lodash", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(blobStore.exists("1", "100", "lodash/package.json")).isTrue();
  }

  @Test
  void publish_nonHosted_throwsException() {
    RepoEntity repo = createRepo("npm-proxy", RepositoryFormat.NPM, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT",
        "/npm/npm-proxy/lodash", "{\"name\":\"lodash\"}");

    assertThatThrownBy(() -> controller.publish("npm-proxy", "lodash", request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
  }

  @Test
  void publish_wrongFormat_throwsException() {
    RepoEntity repo = createRepo("maven-repo", RepositoryFormat.MAVEN, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT",
        "/npm/maven-repo/lodash", "{\"name\":\"lodash\"}");

    assertThatThrownBy(() -> controller.publish("maven-repo", "lodash", request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("npm");
  }

  // ===== Unpublish Tests =====

  @Test
  void unpublish_existing_returns204() {
    RepoEntity repo = createRepo("npm-hosted", RepositoryFormat.NPM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "lodash/package.json", "{\"name\":\"lodash\"}");

    ResponseEntity<?> response = controller.unpublish("npm-hosted", "lodash", "1-abc123");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void unpublish_notFound_returns404() {
    RepoEntity repo = createRepo("npm-hosted", RepositoryFormat.NPM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.unpublish("npm-hosted", "missing-pkg", "1-abc123");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void unpublish_nonHosted_throwsException() {
    RepoEntity repo = createRepo("npm-proxy", RepositoryFormat.NPM, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    assertThatThrownBy(() -> controller.unpublish("npm-proxy", "lodash", "1-abc123"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
  }

  // ===== Set Dist-Tag Tests =====

  @Test
  void setDistTag_success_returns201() throws Exception {
    RepoEntity repo = createRepo("npm-hosted", RepositoryFormat.NPM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT",
        "/npm/npm-hosted/-/package/lodash/dist-tags/latest", "\"4.17.21\"");
    ResponseEntity<?> response = controller.setDistTag("npm-hosted", "lodash", "latest", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(blobStore.exists("1", "100", "lodash/-/dist-tags/latest")).isTrue();
  }

  @Test
  void setDistTag_nonHosted_throwsException() {
    RepoEntity repo = createRepo("npm-proxy", RepositoryFormat.NPM, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT",
        "/npm/npm-proxy/-/package/lodash/dist-tags/latest", "\"4.17.21\"");

    assertThatThrownBy(() -> controller.setDistTag("npm-proxy", "lodash", "latest", request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
  }

  // ===== Search Tests =====

  @Test
  void search_returns200WithJson() {
    RepoEntity repo = createRepo("npm-hosted", RepositoryFormat.NPM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.search("npm-hosted", "lodash", 20);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().toString()).contains("objects");
  }
}
