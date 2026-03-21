package cloud.xcan.angus.core.repo.interfaces.format.protocol;

import static cloud.xcan.angus.core.repo.interfaces.format.protocol.MavenProtocolRestTest.createRepo;
import static cloud.xcan.angus.core.repo.interfaces.format.protocol.MavenProtocolRestTest.createRequestWithBody;
import static cloud.xcan.angus.core.repo.interfaces.format.protocol.MavenProtocolRestTest.injectField;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cloud.xcan.angus.core.repo.domain.format.FormatHandlerRegistry;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryType;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for {@link AptProtocolRest}.
 * Validates Debian APT repository protocol correctness including Release, Packages, and deb
 * package operations.
 */
public class AptProtocolRestTest {

  private AptProtocolRest controller;
  private StubRepositoryQuery repositoryQuery;
  private StubBlobStore blobStore;
  private FormatHandlerRegistry formatHandlerRegistry;

  @BeforeEach
  void setUp() throws Exception {
    controller = new AptProtocolRest();
    repositoryQuery = new StubRepositoryQuery();
    blobStore = new StubBlobStore();
    formatHandlerRegistry = new FormatHandlerRegistry(
        List.of(new StubFormatHandler(RepositoryFormat.APT)));

    injectField(controller, "repositoryQuery", repositoryQuery);
    injectField(controller, "blobStore", blobStore);
    injectField(controller, "formatHandlerRegistry", formatHandlerRegistry);
  }

  // ===== Release Tests =====

  @Test
  void release_existing_returns200() {
    RepoEntity repo = createRepo("apt-hosted", RepositoryFormat.APT, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "dists/jammy/Release", "Origin: apt-hosted");

    ResponseEntity<?> response = controller.release("apt-hosted", "jammy");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void release_notFound_returns404() {
    RepoEntity repo = createRepo("apt-hosted", RepositoryFormat.APT, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.release("apt-hosted", "missing-dist");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void release_wrongFormat_throwsException() {
    RepoEntity repo = createRepo("docker-repo", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    assertThatThrownBy(() -> controller.release("docker-repo", "jammy"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("apt");
  }

  // ===== InRelease Tests =====

  @Test
  void inRelease_existing_returns200() {
    RepoEntity repo = createRepo("apt-hosted", RepositoryFormat.APT, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "dists/jammy/InRelease", "signed-release-content");

    ResponseEntity<?> response = controller.inRelease("apt-hosted", "jammy");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void inRelease_notFound_returns404() {
    RepoEntity repo = createRepo("apt-hosted", RepositoryFormat.APT, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.inRelease("apt-hosted", "missing-dist");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Packages Tests =====

  @Test
  void packages_existing_returns200() {
    RepoEntity repo = createRepo("apt-hosted", RepositoryFormat.APT, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "dists/jammy/main/binary-amd64/Packages",
        "Package: nginx\nVersion: 1.24.0");

    ResponseEntity<?> response = controller.packages("apt-hosted", "jammy", "main", "amd64");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void packages_notFound_returns404() {
    RepoEntity repo = createRepo("apt-hosted", RepositoryFormat.APT, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.packages("apt-hosted", "jammy", "main", "arm64");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Packages.gz Tests =====

  @Test
  void packagesGz_existing_returns200() {
    RepoEntity repo = createRepo("apt-hosted", RepositoryFormat.APT, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "dists/jammy/main/binary-amd64/Packages.gz",
        "gzipped-packages");

    ResponseEntity<?> response = controller.packagesGz("apt-hosted", "jammy", "main", "amd64");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void packagesGz_notFound_returns404() {
    RepoEntity repo = createRepo("apt-hosted", RepositoryFormat.APT, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.packagesGz("apt-hosted", "jammy", "main", "arm64");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Download Deb Tests =====

  @Test
  void downloadDeb_existing_returns200() {
    RepoEntity repo = createRepo("apt-hosted", RepositoryFormat.APT, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "pool/main/n/nginx/nginx_1.24.0-1_amd64.deb",
        "deb-content");

    ResponseEntity<?> response = controller.downloadDeb("apt-hosted", "main", "n", "nginx",
        "nginx_1.24.0-1_amd64.deb");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void downloadDeb_notFound_returns404() {
    RepoEntity repo = createRepo("apt-hosted", RepositoryFormat.APT, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.downloadDeb("apt-hosted", "main", "n", "nginx",
        "nginx_99.0.0_amd64.deb");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Upload Deb Tests =====

  @Test
  void uploadDeb_success_returns201() throws Exception {
    RepoEntity repo = createRepo("apt-hosted", RepositoryFormat.APT, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT",
        "/apt/apt-hosted/pool/nginx_1.24.0-1_amd64.deb", "deb-binary-content");
    ResponseEntity<?> response = controller.uploadDeb("apt-hosted",
        "nginx_1.24.0-1_amd64.deb", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(blobStore.exists("1", "100", "pool/nginx_1.24.0-1_amd64.deb")).isTrue();
  }

  @Test
  void uploadDeb_nonHosted_throwsException() {
    RepoEntity repo = createRepo("apt-proxy", RepositoryFormat.APT, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT",
        "/apt/apt-proxy/pool/nginx_1.24.0-1_amd64.deb", "deb-binary-content");

    assertThatThrownBy(
        () -> controller.uploadDeb("apt-proxy", "nginx_1.24.0-1_amd64.deb", request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
  }

  @Test
  void uploadDeb_wrongFormat_throwsException() {
    RepoEntity repo = createRepo("maven-repo", RepositoryFormat.MAVEN, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT",
        "/apt/maven-repo/pool/nginx.deb", "deb-content");

    assertThatThrownBy(() -> controller.uploadDeb("maven-repo", "nginx.deb", request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("apt");
  }
}
