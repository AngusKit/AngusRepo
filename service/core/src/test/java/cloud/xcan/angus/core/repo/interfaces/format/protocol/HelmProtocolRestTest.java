package cloud.xcan.angus.core.repo.interfaces.format.protocol;

import static cloud.xcan.angus.core.repo.interfaces.format.protocol.MavenProtocolRestTest.createRepo;
import static cloud.xcan.angus.core.repo.interfaces.format.protocol.MavenProtocolRestTest.injectField;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cloud.xcan.angus.core.repo.domain.format.FormatHandlerRegistry;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Unit tests for {@link HelmProtocolRest}.
 * Validates Helm Chart repository protocol correctness including index, chart download, upload, and
 * delete.
 */
public class HelmProtocolRestTest {

  private HelmProtocolRest controller;
  private StubRepositoryQuery repositoryQuery;
  private StubBlobStore blobStore;
  private FormatHandlerRegistry formatHandlerRegistry;

  @BeforeEach
  void setUp() throws Exception {
    controller = new HelmProtocolRest();
    repositoryQuery = new StubRepositoryQuery();
    blobStore = new StubBlobStore();
    formatHandlerRegistry = new FormatHandlerRegistry(
        List.of(new StubFormatHandler(RepositoryFormat.HELM)));

    injectField(controller, "repositoryQuery", repositoryQuery);
    injectField(controller, "blobStore", blobStore);
    injectField(controller, "formatHandlerRegistry", formatHandlerRegistry);
  }

  // ===== Index Tests =====

  @Test
  void index_returns200WithYamlContentType() {
    RepoEntity repo = createRepo("helm-hosted", RepositoryFormat.HELM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.index("helm-hosted");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentType().toString()).contains("yaml");
  }

  @Test
  void index_wrongFormat_throwsException() {
    RepoEntity repo = createRepo("docker-repo", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    assertThatThrownBy(() -> controller.index("docker-repo"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("helm");
  }

  // ===== Download Chart Tests =====

  @Test
  void downloadChart_existing_returns200() {
    RepoEntity repo = createRepo("helm-hosted", RepositoryFormat.HELM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "charts/nginx-1.0.0.tgz", "chart-content");

    ResponseEntity<?> response = controller.downloadChart("helm-hosted", "nginx-1.0.0.tgz");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void downloadChart_notFound_returns404() {
    RepoEntity repo = createRepo("helm-hosted", RepositoryFormat.HELM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.downloadChart("helm-hosted", "missing-1.0.0.tgz");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void downloadChart_wrongFormat_throwsException() {
    RepoEntity repo = createRepo("npm-repo", RepositoryFormat.NPM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    assertThatThrownBy(() -> controller.downloadChart("npm-repo", "nginx-1.0.0.tgz"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("helm");
  }

  // ===== Upload Chart Tests =====

  @Test
  void uploadChart_success_returns201() throws Exception {
    RepoEntity repo = createRepo("helm-hosted", RepositoryFormat.HELM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    MultipartFile chart = createStubMultipartFile("chart", "nginx-1.0.0.tgz",
        "chart-binary".getBytes());
    ResponseEntity<?> response = controller.uploadChart("helm-hosted", chart);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(blobStore.exists("1", "100", "charts/nginx-1.0.0.tgz")).isTrue();
  }

  @Test
  void uploadChart_nonHosted_throwsException() {
    RepoEntity repo = createRepo("helm-proxy", RepositoryFormat.HELM, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    MultipartFile chart = createStubMultipartFile("chart", "nginx-1.0.0.tgz",
        "chart-binary".getBytes());

    assertThatThrownBy(() -> controller.uploadChart("helm-proxy", chart))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
  }

  // ===== Delete Chart Tests =====

  @Test
  void deleteChart_existing_returns204() {
    RepoEntity repo = createRepo("helm-hosted", RepositoryFormat.HELM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "charts/nginx-1.0.0.tgz", "chart-content");

    ResponseEntity<?> response = controller.deleteChart("helm-hosted", "nginx", "1.0.0");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(blobStore.exists("1", "100", "charts/nginx-1.0.0.tgz")).isFalse();
  }

  @Test
  void deleteChart_notFound_returns404() {
    RepoEntity repo = createRepo("helm-hosted", RepositoryFormat.HELM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.deleteChart("helm-hosted", "missing", "1.0.0");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void deleteChart_nonHosted_throwsException() {
    RepoEntity repo = createRepo("helm-proxy", RepositoryFormat.HELM, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    assertThatThrownBy(() -> controller.deleteChart("helm-proxy", "nginx", "1.0.0"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
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
        return "application/gzip";
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
