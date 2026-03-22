package cloud.xcan.angus.core.repo.interfaces.format.protocol;

import static cloud.xcan.angus.core.repo.interfaces.format.protocol.MavenProtocolRestTest.createRepo;
import static cloud.xcan.angus.core.repo.interfaces.format.protocol.MavenProtocolRestTest.createRequest;
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
 * Unit tests for {@link GoProtocolRest}.
 * Validates GOPROXY protocol correctness including version listing, module info, go.mod, and zip
 * download.
 */
public class GoProtocolRestTest {

  private GoProtocolRest controller;
  private StubRepositoryQuery repositoryQuery;
  private StubBlobStore blobStore;
  private FormatHandlerRegistry formatHandlerRegistry;

  @BeforeEach
  void setUp() throws Exception {
    controller = new GoProtocolRest();
    repositoryQuery = new StubRepositoryQuery();
    blobStore = new StubBlobStore();
    formatHandlerRegistry = new FormatHandlerRegistry(
        List.of(new StubFormatHandler(RepositoryFormat.GO)));

    injectField(controller, "repositoryQuery", repositoryQuery);
    injectField(controller, "blobStore", blobStore);
    injectField(controller, "formatHandlerRegistry", formatHandlerRegistry);
  }

  // ===== List Versions Tests =====

  @Test
  void listVersions_existing_returns200() {
    RepoEntity repo = createRepo("go-hosted", RepositoryFormat.GO, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "github.com/user/repo/@v/list", "v1.0.0\nv1.1.0\n");

    HttpServletRequest request = createRequest("GET",
        "/go/go-hosted/github.com/user/repo/@v/list");
    ResponseEntity<?> response = controller.listVersions("go-hosted", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void listVersions_notFound_returns404() {
    RepoEntity repo = createRepo("go-hosted", RepositoryFormat.GO, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequest("GET",
        "/go/go-hosted/github.com/missing/module/@v/list");
    ResponseEntity<?> response = controller.listVersions("go-hosted", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void listVersions_wrongFormat_throwsException() {
    RepoEntity repo = createRepo("npm-repo", RepositoryFormat.NPM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequest("GET",
        "/go/npm-repo/github.com/user/repo/@v/list");

    assertThatThrownBy(() -> controller.listVersions("npm-repo", request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("go");
  }

  // ===== Version Info Tests =====

  @Test
  void versionInfo_existing_returns200() {
    RepoEntity repo = createRepo("go-hosted", RepositoryFormat.GO, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "github.com/user/repo/@v/v1.0.0.info",
        "{\"Version\":\"v1.0.0\"}");

    HttpServletRequest request = createRequest("GET",
        "/go/go-hosted/github.com/user/repo/@v/v1.0.0.info");
    ResponseEntity<?> response = controller.versionInfo("go-hosted", "v1.0.0", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void versionInfo_notFound_returns404() {
    RepoEntity repo = createRepo("go-hosted", RepositoryFormat.GO, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequest("GET",
        "/go/go-hosted/github.com/user/repo/@v/v99.0.0.info");
    ResponseEntity<?> response = controller.versionInfo("go-hosted", "v99.0.0", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Go Mod Tests =====

  @Test
  void goMod_existing_returns200() {
    RepoEntity repo = createRepo("go-hosted", RepositoryFormat.GO, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "github.com/user/repo/@v/v1.0.0.mod",
        "module github.com/user/repo");

    HttpServletRequest request = createRequest("GET",
        "/go/go-hosted/github.com/user/repo/@v/v1.0.0.mod");
    ResponseEntity<?> response = controller.goMod("go-hosted", "v1.0.0", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void goMod_notFound_returns404() {
    RepoEntity repo = createRepo("go-hosted", RepositoryFormat.GO, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequest("GET",
        "/go/go-hosted/github.com/user/repo/@v/v99.0.0.mod");
    ResponseEntity<?> response = controller.goMod("go-hosted", "v99.0.0", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Download Zip Tests =====

  @Test
  void downloadZip_existing_returns200() {
    RepoEntity repo = createRepo("go-hosted", RepositoryFormat.GO, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "github.com/user/repo/@v/v1.0.0.zip", "zip-content");

    HttpServletRequest request = createRequest("GET",
        "/go/go-hosted/github.com/user/repo/@v/v1.0.0.zip");
    ResponseEntity<?> response = controller.downloadZip("go-hosted", "v1.0.0", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void downloadZip_notFound_returns404() {
    RepoEntity repo = createRepo("go-hosted", RepositoryFormat.GO, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequest("GET",
        "/go/go-hosted/github.com/user/repo/@v/v99.0.0.zip");
    ResponseEntity<?> response = controller.downloadZip("go-hosted", "v99.0.0", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Latest Tests =====

  @Test
  void latest_existing_returns200() {
    RepoEntity repo = createRepo("go-hosted", RepositoryFormat.GO, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "github.com/user/repo/@latest",
        "{\"Version\":\"v1.1.0\"}");

    HttpServletRequest request = createRequest("GET",
        "/go/go-hosted/github.com/user/repo/@latest");
    ResponseEntity<?> response = controller.latest("go-hosted", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void latest_notFound_returns404() {
    RepoEntity repo = createRepo("go-hosted", RepositoryFormat.GO, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequest("GET",
        "/go/go-hosted/github.com/missing/module/@latest");
    ResponseEntity<?> response = controller.latest("go-hosted", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
