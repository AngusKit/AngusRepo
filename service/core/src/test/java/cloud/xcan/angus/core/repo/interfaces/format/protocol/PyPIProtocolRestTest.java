package cloud.xcan.angus.core.repo.interfaces.format.protocol;

import static cloud.xcan.angus.core.repo.interfaces.format.protocol.MavenProtocolRestTest.createRepo;
import static cloud.xcan.angus.core.repo.interfaces.format.protocol.MavenProtocolRestTest.injectField;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Unit tests for {@link PyPIProtocolRest}.
 * Validates PyPI (PEP 503) protocol correctness including simple index, package pages, and uploads.
 */
public class PyPIProtocolRestTest {

  private PyPIProtocolRest controller;
  private StubRepositoryQuery repositoryQuery;
  private StubBlobStore blobStore;

  @BeforeEach
  void setUp() throws Exception {
    controller = new PyPIProtocolRest();
    repositoryQuery = new StubRepositoryQuery();
    blobStore = new StubBlobStore();

    injectField(controller, "repositoryQuery", repositoryQuery);
    injectField(controller, "blobStore", blobStore);
  }

  // ===== Simple Index Tests =====

  @Test
  void simpleIndex_withStoredIndex_returns200() {
    RepoEntity repo = createRepo("pypi-hosted", RepositoryFormat.PYPI, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "simple/index.html", "<html><body>index</body></html>");

    ResponseEntity<?> response = controller.simpleIndex("pypi-hosted");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void simpleIndex_withoutStoredIndex_returnsGeneratedIndex() {
    RepoEntity repo = createRepo("pypi-hosted", RepositoryFormat.PYPI, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.simpleIndex("pypi-hosted");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().toString()).contains("Simple Index");
  }

  @Test
  void simpleIndex_wrongFormat_throwsException() {
    RepoEntity repo = createRepo("docker-repo", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    assertThatThrownBy(() -> controller.simpleIndex("docker-repo"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("pypi");
  }

  // ===== Package Page Tests =====

  @Test
  void packagePage_existing_returns200() {
    RepoEntity repo = createRepo("pypi-hosted", RepositoryFormat.PYPI, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "simple/requests/index.html",
        "<html><body>requests</body></html>");

    ResponseEntity<?> response = controller.packagePage("pypi-hosted", "requests");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void packagePage_notFound_returns404() {
    RepoEntity repo = createRepo("pypi-hosted", RepositoryFormat.PYPI, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.packagePage("pypi-hosted", "missing-pkg");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void packagePage_wrongFormat_throwsException() {
    RepoEntity repo = createRepo("npm-repo", RepositoryFormat.NPM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    assertThatThrownBy(() -> controller.packagePage("npm-repo", "requests"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("pypi");
  }

  // ===== Upload Tests =====

  @Test
  void upload_success_returns201() throws Exception {
    RepoEntity repo = createRepo("pypi-hosted", RepositoryFormat.PYPI, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    MultipartFile file = createStubMultipartFile("content", "requests-2.31.0.tar.gz",
        "package-content".getBytes());
    ResponseEntity<?> response = controller.upload("pypi-hosted", file, "requests", "2.31.0");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  void upload_nonHosted_throwsException() {
    RepoEntity repo = createRepo("pypi-proxy", RepositoryFormat.PYPI, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    MultipartFile file = createStubMultipartFile("content", "requests-2.31.0.tar.gz",
        "package-content".getBytes());

    assertThatThrownBy(() -> controller.upload("pypi-proxy", file, "requests", "2.31.0"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
  }

  // ===== Download Tests =====

  @Test
  void download_existing_returns200() {
    RepoEntity repo = createRepo("pypi-hosted", RepositoryFormat.PYPI, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "packages/requests/2.31.0/requests-2.31.0.tar.gz",
        "package-data");

    ResponseEntity<?> response = controller.download("pypi-hosted", "requests", "2.31.0",
        "requests-2.31.0.tar.gz");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void download_notFound_returns404() {
    RepoEntity repo = createRepo("pypi-hosted", RepositoryFormat.PYPI, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.download("pypi-hosted", "requests", "99.0.0",
        "requests-99.0.0.tar.gz");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Package JSON Tests =====

  @Test
  void packageJson_existing_returns200() {
    RepoEntity repo = createRepo("pypi-hosted", RepositoryFormat.PYPI, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "pypi/requests/json", "{\"info\":{\"name\":\"requests\"}}");

    ResponseEntity<?> response = controller.packageJson("pypi-hosted", "requests");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void packageJson_notFound_returns404() {
    RepoEntity repo = createRepo("pypi-hosted", RepositoryFormat.PYPI, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.packageJson("pypi-hosted", "missing-pkg");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Version JSON Tests =====

  @Test
  void versionJson_existing_returns200() {
    RepoEntity repo = createRepo("pypi-hosted", RepositoryFormat.PYPI, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "pypi/requests/2.31.0/json",
        "{\"info\":{\"version\":\"2.31.0\"}}");

    ResponseEntity<?> response = controller.versionJson("pypi-hosted", "requests", "2.31.0");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void versionJson_notFound_returns404() {
    RepoEntity repo = createRepo("pypi-hosted", RepositoryFormat.PYPI, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.versionJson("pypi-hosted", "requests", "99.0.0");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Helper methods =====

  private MultipartFile createStubMultipartFile(String name, String originalFilename,
      byte[] content) {
    return new MultipartFile() {
      @Override
      public String getName() {
        return name;
      }

      @Override
      public String getOriginalFilename() {
        return originalFilename;
      }

      @Override
      public String getContentType() {
        return "application/octet-stream";
      }

      @Override
      public boolean isEmpty() {
        return content == null || content.length == 0;
      }

      @Override
      public long getSize() {
        return content != null ? content.length : 0;
      }

      @Override
      public byte[] getBytes() {
        return content != null ? content : new byte[0];
      }

      @Override
      public InputStream getInputStream() {
        return new ByteArrayInputStream(getBytes());
      }

      @Override
      public void transferTo(java.io.File dest) {
      }
    };
  }
}
