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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Unit tests for {@link YumProtocolRest}.
 * Validates YUM/DNF repository protocol correctness including repomd.xml, repodata, and RPM
 * package operations.
 */
public class YumProtocolRestTest {

  private YumProtocolRest controller;
  private StubRepositoryQuery repositoryQuery;
  private StubBlobStore blobStore;
  private FormatHandlerRegistry formatHandlerRegistry;

  @BeforeEach
  void setUp() throws Exception {
    controller = new YumProtocolRest();
    repositoryQuery = new StubRepositoryQuery();
    blobStore = new StubBlobStore();
    formatHandlerRegistry = new FormatHandlerRegistry(
        List.of(new StubFormatHandler(RepositoryFormat.YUM)));

    injectField(controller, "repositoryQuery", repositoryQuery);
    injectField(controller, "blobStore", blobStore);
    injectField(controller, "formatHandlerRegistry", formatHandlerRegistry);
  }

  // ===== Repomd Tests =====

  @Test
  void repomd_withStoredFile_returns200() {
    RepoEntity repo = createRepo("yum-hosted", RepositoryFormat.YUM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "repodata/repomd.xml", "<repomd>stored</repomd>");

    ResponseEntity<?> response = controller.repomd("yum-hosted");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_XML);
  }

  @Test
  void repomd_withoutStoredFile_generatesIndex() {
    RepoEntity repo = createRepo("yum-hosted", RepositoryFormat.YUM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.repomd("yum-hosted");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_XML);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void repomd_wrongFormat_throwsException() {
    RepoEntity repo = createRepo("docker-repo", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    assertThatThrownBy(() -> controller.repomd("docker-repo"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("yum");
  }

  // ===== Repodata Tests =====

  @Test
  void repodata_existing_returns200() {
    RepoEntity repo = createRepo("yum-hosted", RepositoryFormat.YUM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "repodata/primary.xml.gz", "gzipped-primary-data");

    ResponseEntity<?> response = controller.repodata("yum-hosted", "primary.xml.gz");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void repodata_notFound_returns404() {
    RepoEntity repo = createRepo("yum-hosted", RepositoryFormat.YUM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.repodata("yum-hosted", "missing.xml.gz");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Download RPM Tests =====

  @Test
  void downloadRpm_existing_returns200() {
    RepoEntity repo = createRepo("yum-hosted", RepositoryFormat.YUM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "Packages/n/nginx-1.24.0-1.el8.x86_64.rpm", "rpm-content");

    ResponseEntity<?> response = controller.downloadRpm("yum-hosted", "n",
        "nginx-1.24.0-1.el8.x86_64.rpm");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void downloadRpm_notFound_returns404() {
    RepoEntity repo = createRepo("yum-hosted", RepositoryFormat.YUM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.downloadRpm("yum-hosted", "n",
        "missing-1.0.0.rpm");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Upload RPM Tests =====

  @Test
  void uploadRpm_success_returns201() throws Exception {
    RepoEntity repo = createRepo("yum-hosted", RepositoryFormat.YUM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    MultipartFile file = createStubMultipartFile("file", "nginx-1.24.0-1.el8.x86_64.rpm",
        "rpm-binary".getBytes());
    ResponseEntity<?> response = controller.uploadRpm("yum-hosted", file);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(blobStore.exists("1", "100", "Packages/n/nginx-1.24.0-1.el8.x86_64.rpm"))
        .isTrue();
  }

  @Test
  void uploadRpm_nonHosted_throwsException() {
    RepoEntity repo = createRepo("yum-proxy", RepositoryFormat.YUM, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    MultipartFile file = createStubMultipartFile("file", "nginx-1.24.0-1.el8.x86_64.rpm",
        "rpm-binary".getBytes());

    assertThatThrownBy(() -> controller.uploadRpm("yum-proxy", file))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
  }

  // ===== Delete RPM Tests =====

  @Test
  void deleteRpm_existing_returns204() {
    RepoEntity repo = createRepo("yum-hosted", RepositoryFormat.YUM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "Packages/n/nginx-1.24.0-1.el8.x86_64.rpm", "rpm-content");

    ResponseEntity<?> response = controller.deleteRpm("yum-hosted", "n",
        "nginx-1.24.0-1.el8.x86_64.rpm");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(blobStore.exists("1", "100", "Packages/n/nginx-1.24.0-1.el8.x86_64.rpm"))
        .isFalse();
  }

  @Test
  void deleteRpm_notFound_returns404() {
    RepoEntity repo = createRepo("yum-hosted", RepositoryFormat.YUM, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.deleteRpm("yum-hosted", "n",
        "missing-1.0.0.rpm");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void deleteRpm_nonHosted_throwsException() {
    RepoEntity repo = createRepo("yum-proxy", RepositoryFormat.YUM, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    assertThatThrownBy(
        () -> controller.deleteRpm("yum-proxy", "n", "nginx-1.24.0-1.el8.x86_64.rpm"))
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
        return "application/x-rpm";
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
