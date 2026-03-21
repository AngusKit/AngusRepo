package cloud.xcan.angus.core.repo.interfaces.format.protocol;

import static cloud.xcan.angus.core.repo.interfaces.format.protocol.MavenProtocolRestTest.createRepo;
import static cloud.xcan.angus.core.repo.interfaces.format.protocol.MavenProtocolRestTest.createRequest;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for {@link RawProtocolRest}.
 * Validates raw file storage protocol correctness including download, upload, delete, and content
 * type resolution for various file extensions.
 */
public class RawProtocolRestTest {

  private RawProtocolRest controller;
  private StubRepositoryQuery repositoryQuery;
  private StubBlobStore blobStore;
  private FormatHandlerRegistry formatHandlerRegistry;

  @BeforeEach
  void setUp() throws Exception {
    controller = new RawProtocolRest();
    repositoryQuery = new StubRepositoryQuery();
    blobStore = new StubBlobStore();
    formatHandlerRegistry = new FormatHandlerRegistry(
        List.of(new StubFormatHandler(RepositoryFormat.RAW)));

    injectField(controller, "repositoryQuery", repositoryQuery);
    injectField(controller, "blobStore", blobStore);
    injectField(controller, "formatHandlerRegistry", formatHandlerRegistry);
  }

  // ===== Download Tests =====

  @Test
  void download_existingFile_returns200() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "docs/readme.txt", "file content");

    HttpServletRequest request = createRequest("GET", "/raw/raw-hosted/docs/readme.txt");
    ResponseEntity<?> response = controller.download("raw-hosted", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void download_notFound_returns404() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequest("GET", "/raw/raw-hosted/missing/file.txt");
    ResponseEntity<?> response = controller.download("raw-hosted", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void download_wrongFormat_throwsException() {
    RepoEntity repo = createRepo("docker-repo", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequest("GET", "/raw/docker-repo/some/file.txt");

    assertThatThrownBy(() -> controller.download("docker-repo", request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("raw");
  }

  // ===== Upload Tests =====

  @Test
  void upload_success_returns201() throws Exception {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT", "/raw/raw-hosted/docs/readme.txt",
        "file content");
    ResponseEntity<?> response = controller.upload("raw-hosted", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(blobStore.exists("1", "100", "docs/readme.txt")).isTrue();
  }

  @Test
  void upload_nonHosted_throwsException() {
    RepoEntity repo = createRepo("raw-proxy", RepositoryFormat.RAW, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT", "/raw/raw-proxy/docs/readme.txt",
        "file content");

    assertThatThrownBy(() -> controller.upload("raw-proxy", request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
  }

  // ===== Delete Tests =====

  @Test
  void delete_existing_returns204() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "docs/readme.txt", "file content");

    HttpServletRequest request = createRequest("DELETE", "/raw/raw-hosted/docs/readme.txt");
    ResponseEntity<?> response = controller.delete("raw-hosted", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(blobStore.exists("1", "100", "docs/readme.txt")).isFalse();
  }

  @Test
  void delete_notFound_returns404() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequest("DELETE", "/raw/raw-hosted/missing/file.txt");
    ResponseEntity<?> response = controller.delete("raw-hosted", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_nonHosted_throwsException() {
    RepoEntity repo = createRepo("raw-proxy", RepositoryFormat.RAW, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequest("DELETE", "/raw/raw-proxy/docs/readme.txt");

    assertThatThrownBy(() -> controller.delete("raw-proxy", request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
  }

  // ===== Exists (HEAD) Tests =====

  @Test
  void exists_existingFile_returns200() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "docs/readme.txt", "file content");

    HttpServletRequest request = createRequest("HEAD", "/raw/raw-hosted/docs/readme.txt");
    ResponseEntity<?> response = controller.exists("raw-hosted", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentLength()).isGreaterThan(0);
  }

  @Test
  void exists_notFound_returns404() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequest("HEAD", "/raw/raw-hosted/missing/file.txt");
    ResponseEntity<?> response = controller.exists("raw-hosted", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Content Type Resolution Tests =====

  @Test
  void download_htmlFile_returnsTextHtml() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "page.html", "<html/>");

    HttpServletRequest request = createRequest("GET", "/raw/raw-hosted/page.html");
    ResponseEntity<?> response = controller.download("raw-hosted", request);

    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.TEXT_HTML);
  }

  @Test
  void download_jsonFile_returnsApplicationJson() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "data.json", "{}");

    HttpServletRequest request = createRequest("GET", "/raw/raw-hosted/data.json");
    ResponseEntity<?> response = controller.download("raw-hosted", request);

    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
  }

  @Test
  void download_xmlFile_returnsApplicationXml() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "config.xml", "<config/>");

    HttpServletRequest request = createRequest("GET", "/raw/raw-hosted/config.xml");
    ResponseEntity<?> response = controller.download("raw-hosted", request);

    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_XML);
  }

  @Test
  void download_txtFile_returnsTextPlain() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "notes.txt", "notes");

    HttpServletRequest request = createRequest("GET", "/raw/raw-hosted/notes.txt");
    ResponseEntity<?> response = controller.download("raw-hosted", request);

    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.TEXT_PLAIN);
  }

  @Test
  void download_cssFile_returnsTextCss() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "style.css", "body{}");

    HttpServletRequest request = createRequest("GET", "/raw/raw-hosted/style.css");
    ResponseEntity<?> response = controller.download("raw-hosted", request);

    assertThat(response.getHeaders().getContentType().toString()).contains("text/css");
  }

  @Test
  void download_jsFile_returnsApplicationJavascript() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "app.js", "var x=1;");

    HttpServletRequest request = createRequest("GET", "/raw/raw-hosted/app.js");
    ResponseEntity<?> response = controller.download("raw-hosted", request);

    assertThat(response.getHeaders().getContentType().toString())
        .contains("application/javascript");
  }

  @Test
  void download_pngFile_returnsImagePng() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "logo.png", "png-data");

    HttpServletRequest request = createRequest("GET", "/raw/raw-hosted/logo.png");
    ResponseEntity<?> response = controller.download("raw-hosted", request);

    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_PNG);
  }

  @Test
  void download_jpgFile_returnsImageJpeg() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "photo.jpg", "jpg-data");

    HttpServletRequest request = createRequest("GET", "/raw/raw-hosted/photo.jpg");
    ResponseEntity<?> response = controller.download("raw-hosted", request);

    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_JPEG);
  }

  @Test
  void download_gifFile_returnsImageGif() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "anim.gif", "gif-data");

    HttpServletRequest request = createRequest("GET", "/raw/raw-hosted/anim.gif");
    ResponseEntity<?> response = controller.download("raw-hosted", request);

    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_GIF);
  }

  @Test
  void download_svgFile_returnsImageSvgXml() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "icon.svg", "<svg/>");

    HttpServletRequest request = createRequest("GET", "/raw/raw-hosted/icon.svg");
    ResponseEntity<?> response = controller.download("raw-hosted", request);

    assertThat(response.getHeaders().getContentType().toString()).contains("image/svg+xml");
  }

  @Test
  void download_pdfFile_returnsApplicationPdf() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "doc.pdf", "pdf-data");

    HttpServletRequest request = createRequest("GET", "/raw/raw-hosted/doc.pdf");
    ResponseEntity<?> response = controller.download("raw-hosted", request);

    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
  }

  @Test
  void download_zipFile_returnsApplicationZip() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "archive.zip", "zip-data");

    HttpServletRequest request = createRequest("GET", "/raw/raw-hosted/archive.zip");
    ResponseEntity<?> response = controller.download("raw-hosted", request);

    assertThat(response.getHeaders().getContentType().toString()).contains("application/zip");
  }

  @Test
  void download_gzFile_returnsApplicationGzip() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "data.tar.gz", "gzip-data");

    HttpServletRequest request = createRequest("GET", "/raw/raw-hosted/data.tar.gz");
    ResponseEntity<?> response = controller.download("raw-hosted", request);

    assertThat(response.getHeaders().getContentType().toString()).contains("application/gzip");
  }

  @Test
  void download_tarFile_returnsApplicationXTar() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "archive.tar", "tar-data");

    HttpServletRequest request = createRequest("GET", "/raw/raw-hosted/archive.tar");
    ResponseEntity<?> response = controller.download("raw-hosted", request);

    assertThat(response.getHeaders().getContentType().toString()).contains("application/x-tar");
  }

  @Test
  void download_yamlFile_returnsApplicationXYaml() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "config.yaml", "key: value");

    HttpServletRequest request = createRequest("GET", "/raw/raw-hosted/config.yaml");
    ResponseEntity<?> response = controller.download("raw-hosted", request);

    assertThat(response.getHeaders().getContentType().toString()).contains("yaml");
  }

  @Test
  void download_unknownExtension_returnsOctetStream() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "data.xyz", "binary-data");

    HttpServletRequest request = createRequest("GET", "/raw/raw-hosted/data.xyz");
    ResponseEntity<?> response = controller.download("raw-hosted", request);

    assertThat(response.getHeaders().getContentType())
        .isEqualTo(MediaType.APPLICATION_OCTET_STREAM);
  }

  @Test
  void exists_contentTypeCheck_returnsCorrectType() {
    RepoEntity repo = createRepo("raw-hosted", RepositoryFormat.RAW, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "data.json", "{}");

    HttpServletRequest request = createRequest("HEAD", "/raw/raw-hosted/data.json");
    ResponseEntity<?> response = controller.exists("raw-hosted", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
  }
}
