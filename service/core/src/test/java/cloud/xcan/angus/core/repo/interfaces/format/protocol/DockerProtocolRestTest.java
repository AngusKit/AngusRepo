package cloud.xcan.angus.core.repo.interfaces.format.protocol;

import static cloud.xcan.angus.core.repo.interfaces.format.protocol.MavenProtocolRestTest.createRepo;
import static cloud.xcan.angus.core.repo.interfaces.format.protocol.MavenProtocolRestTest.createRequest;
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
 * Unit tests for {@link DockerProtocolRest}.
 * Validates Docker Registry V2 protocol correctness including manifests, blobs, and uploads.
 */
public class DockerProtocolRestTest {

  private DockerProtocolRest controller;
  private StubRepositoryQuery repositoryQuery;
  private StubBlobStore blobStore;

  @BeforeEach
  void setUp() throws Exception {
    controller = new DockerProtocolRest();
    repositoryQuery = new StubRepositoryQuery();
    blobStore = new StubBlobStore();

    injectField(controller, "repositoryQuery", repositoryQuery);
    injectField(controller, "blobStore", blobStore);
  }

  // ===== Version Check Tests =====

  @Test
  void versionCheck_returns200WithApiVersionHeader() {
    ResponseEntity<?> response = controller.versionCheck();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getFirst("Docker-Distribution-Api-Version"))
        .isEqualTo("registry/2.0");
  }

  // ===== Catalog Tests =====

  @Test
  void catalog_returns200WithRepositoriesJson() {
    ResponseEntity<?> response = controller.catalog();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().toString()).contains("repositories");
  }

  // ===== List Tags Tests =====

  @Test
  void listTags_validRepo_returns200() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.listTags("my-docker");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().toString()).contains("my-docker");
  }

  @Test
  void listTags_wrongFormat_throwsException() {
    RepoEntity repo = createRepo("maven-repo", RepositoryFormat.MAVEN, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    assertThatThrownBy(() -> controller.listTags("maven-repo"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("docker");
  }

  // ===== Check Manifest Tests =====

  @Test
  void checkManifest_existing_returns200() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "manifests/v1.0", "manifest-content");

    ResponseEntity<?> response = controller.checkManifest("my-docker", "v1.0");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentLength()).isGreaterThan(0);
  }

  @Test
  void checkManifest_notFound_returns404() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.checkManifest("my-docker", "v1.0");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void checkManifest_wrongFormat_throwsException() {
    RepoEntity repo = createRepo("npm-repo", RepositoryFormat.NPM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    assertThatThrownBy(() -> controller.checkManifest("npm-repo", "v1.0"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("docker");
  }

  // ===== Get Manifest Tests =====

  @Test
  void getManifest_existing_returns200WithContent() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "manifests/sha256:abc123", "manifest-body");

    ResponseEntity<?> response = controller.getManifest("my-docker", "sha256:abc123");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void getManifest_notFound_returns404() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.getManifest("my-docker", "sha256:missing");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void getManifest_wrongFormat_throwsException() {
    RepoEntity repo = createRepo("helm-repo", RepositoryFormat.HELM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    assertThatThrownBy(() -> controller.getManifest("helm-repo", "v1.0"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("docker");
  }

  // ===== Upload Manifest Tests =====

  @Test
  void uploadManifest_success_returns201() throws Exception {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT",
        "/v2/my-docker/manifests/v1.0", "manifest-content");
    ResponseEntity<?> response = controller.uploadManifest("my-docker", "v1.0", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(blobStore.exists("1", "100", "manifests/v1.0")).isTrue();
  }

  @Test
  void uploadManifest_nonHosted_throwsException() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT",
        "/v2/my-docker/manifests/v1.0", "manifest-content");

    assertThatThrownBy(() -> controller.uploadManifest("my-docker", "v1.0", request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
  }

  @Test
  void uploadManifest_wrongFormat_throwsException() {
    RepoEntity repo = createRepo("raw-repo", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT",
        "/v2/raw-repo/manifests/v1.0", "manifest-content");

    assertThatThrownBy(() -> controller.uploadManifest("raw-repo", "v1.0", request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("docker");
  }

  // ===== Delete Manifest Tests =====

  @Test
  void deleteManifest_existing_returns202() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "manifests/v1.0", "manifest-content");

    ResponseEntity<?> response = controller.deleteManifest("my-docker", "v1.0");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    assertThat(blobStore.exists("1", "100", "manifests/v1.0")).isFalse();
  }

  @Test
  void deleteManifest_notFound_returns404() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.deleteManifest("my-docker", "v1.0");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void deleteManifest_nonHosted_throwsException() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    assertThatThrownBy(() -> controller.deleteManifest("my-docker", "v1.0"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
  }

  // ===== Check Blob Tests =====

  @Test
  void checkBlob_existing_returns200() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "blobs/sha256:layerhash", "layer-data");

    ResponseEntity<?> response = controller.checkBlob("my-docker", "sha256:layerhash");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentLength()).isGreaterThan(0);
  }

  @Test
  void checkBlob_notFound_returns404() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.checkBlob("my-docker", "sha256:missing");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Download Blob Tests =====

  @Test
  void downloadBlob_existing_returns200() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "blobs/sha256:layerhash", "layer-data");

    ResponseEntity<?> response = controller.downloadBlob("my-docker", "sha256:layerhash");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void downloadBlob_notFound_returns404() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.downloadBlob("my-docker", "sha256:missing");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Delete Blob Tests =====

  @Test
  void deleteBlob_existing_returns202() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "blobs/sha256:layerhash", "layer-data");

    ResponseEntity<?> response = controller.deleteBlob("my-docker", "sha256:layerhash");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
  }

  @Test
  void deleteBlob_notFound_returns404() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.deleteBlob("my-docker", "sha256:missing");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void deleteBlob_nonHosted_throwsException() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    assertThatThrownBy(() -> controller.deleteBlob("my-docker", "sha256:layerhash"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
  }

  // ===== Start Upload Tests =====

  @Test
  void startUpload_success_returns202WithUuid() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.startUpload("my-docker");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    assertThat(response.getHeaders().getFirst("Docker-Upload-UUID")).isNotNull();
  }

  @Test
  void startUpload_nonHosted_throwsException() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    assertThatThrownBy(() -> controller.startUpload("my-docker"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
  }

  // ===== Upload Chunk Tests =====

  @Test
  void uploadChunk_success_returns202() throws Exception {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PATCH",
        "/v2/my-docker/blobs/uploads/test-uuid", "chunk-data");
    ResponseEntity<?> response = controller.uploadChunk("my-docker", "test-uuid", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    assertThat(response.getHeaders().getFirst("Docker-Upload-UUID")).isEqualTo("test-uuid");
  }

  @Test
  void uploadChunk_nonHosted_throwsException() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PATCH",
        "/v2/my-docker/blobs/uploads/test-uuid", "chunk-data");

    assertThatThrownBy(() -> controller.uploadChunk("my-docker", "test-uuid", request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
  }

  // ===== Complete Upload Tests =====

  @Test
  void completeUpload_success_returns201() throws Exception {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT",
        "/v2/my-docker/blobs/uploads/test-uuid?digest=sha256:abc123", "final-data");
    ResponseEntity<?> response = controller.completeUpload("my-docker", "test-uuid",
        "sha256:abc123", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(blobStore.exists("1", "100", "blobs/sha256:abc123")).isTrue();
  }

  @Test
  void completeUpload_nonHosted_throwsException() {
    RepoEntity repo = createRepo("my-docker", RepositoryFormat.DOCKER, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT",
        "/v2/my-docker/blobs/uploads/test-uuid", "final-data");

    assertThatThrownBy(
        () -> controller.completeUpload("my-docker", "test-uuid", "sha256:abc123", request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
  }
}
