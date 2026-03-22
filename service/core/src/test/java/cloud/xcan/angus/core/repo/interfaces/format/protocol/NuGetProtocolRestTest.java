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
 * Unit tests for {@link NuGetProtocolRest}.
 * Validates NuGet V3 protocol correctness including service index, search, registration, and push.
 */
public class NuGetProtocolRestTest {

  private NuGetProtocolRest controller;
  private StubRepositoryQuery repositoryQuery;
  private StubBlobStore blobStore;

  @BeforeEach
  void setUp() throws Exception {
    controller = new NuGetProtocolRest();
    repositoryQuery = new StubRepositoryQuery();
    blobStore = new StubBlobStore();

    injectField(controller, "repositoryQuery", repositoryQuery);
    injectField(controller, "blobStore", blobStore);
  }

  // ===== Service Index Tests =====

  @Test
  void serviceIndex_returns200WithServiceIndexJson() {
    RepoEntity repo = createRepo("nuget-hosted", RepositoryFormat.NUGET, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequest("GET",
        "/nuget/nuget-hosted/v3/index.json");
    ResponseEntity<?> response = controller.serviceIndex("nuget-hosted", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().toString()).contains("resources");
  }

  @Test
  void serviceIndex_wrongFormat_throwsException() {
    RepoEntity repo = createRepo("docker-repo", RepositoryFormat.DOCKER, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequest("GET",
        "/nuget/docker-repo/v3/index.json");

    assertThatThrownBy(() -> controller.serviceIndex("docker-repo", request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("nuget");
  }

  // ===== Search Tests =====

  @Test
  void search_returns200WithJson() {
    RepoEntity repo = createRepo("nuget-hosted", RepositoryFormat.NUGET, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.search("nuget-hosted", "Newtonsoft", 0, 20);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().toString()).contains("totalHits");
  }

  // ===== Registration Tests =====

  @Test
  void registration_existing_returns200() {
    RepoEntity repo = createRepo("nuget-hosted", RepositoryFormat.NUGET, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "v3/registration/newtonsoft.json/index.json",
        "{\"items\":[]}");

    ResponseEntity<?> response = controller.registration("nuget-hosted", "Newtonsoft.Json");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void registration_notFound_returns404() {
    RepoEntity repo = createRepo("nuget-hosted", RepositoryFormat.NUGET, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.registration("nuget-hosted", "Missing.Package");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void registration_wrongFormat_throwsException() {
    RepoEntity repo = createRepo("maven-repo", RepositoryFormat.MAVEN, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    assertThatThrownBy(() -> controller.registration("maven-repo", "Newtonsoft.Json"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("nuget");
  }

  // ===== Package Versions Tests =====

  @Test
  void packageVersions_existing_returns200() {
    RepoEntity repo = createRepo("nuget-hosted", RepositoryFormat.NUGET, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100", "v3/flatcontainer/newtonsoft.json/index.json",
        "{\"versions\":[\"13.0.3\"]}");

    ResponseEntity<?> response = controller.packageVersions("nuget-hosted", "Newtonsoft.Json");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void packageVersions_notFound_returns404() {
    RepoEntity repo = createRepo("nuget-hosted", RepositoryFormat.NUGET, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.packageVersions("nuget-hosted", "Missing.Package");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Download Tests =====

  @Test
  void download_existing_returns200() {
    RepoEntity repo = createRepo("nuget-hosted", RepositoryFormat.NUGET, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100",
        "v3/flatcontainer/newtonsoft.json/13.0.3/newtonsoft.json.13.0.3.nupkg",
        "nupkg-content");

    HttpServletRequest request = createRequest("GET",
        "/nuget/nuget-hosted/v3/flatcontainer/Newtonsoft.Json/13.0.3/Newtonsoft.Json.13.0.3.nupkg");
    ResponseEntity<?> response = controller.download("nuget-hosted", "Newtonsoft.Json", "13.0.3",
        request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void download_notFound_returns404() {
    RepoEntity repo = createRepo("nuget-hosted", RepositoryFormat.NUGET, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequest("GET",
        "/nuget/nuget-hosted/v3/flatcontainer/Missing.Pkg/1.0.0/Missing.Pkg.1.0.0.nupkg");
    ResponseEntity<?> response = controller.download("nuget-hosted", "Missing.Pkg", "1.0.0",
        request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // ===== Push Tests =====

  @Test
  void push_success_returns201() throws Exception {
    RepoEntity repo = createRepo("nuget-hosted", RepositoryFormat.NUGET, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT",
        "/nuget/nuget-hosted/api/v2/package", "nupkg-binary-content");
    ResponseEntity<?> response = controller.push("nuget-hosted", request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  void push_nonHosted_throwsException() {
    RepoEntity repo = createRepo("nuget-proxy", RepositoryFormat.NUGET, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    HttpServletRequest request = createRequestWithBody("PUT",
        "/nuget/nuget-proxy/api/v2/package", "nupkg-binary-content");

    assertThatThrownBy(() -> controller.push("nuget-proxy", request))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
  }

  // ===== Delete Tests =====

  @Test
  void delete_existing_returns204() {
    RepoEntity repo = createRepo("nuget-hosted", RepositoryFormat.NUGET, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);
    blobStore.addBlob("1", "100",
        "v3/flatcontainer/newtonsoft.json/13.0.3/newtonsoft.json.13.0.3.nupkg",
        "nupkg-content");

    ResponseEntity<?> response = controller.delete("nuget-hosted", "Newtonsoft.Json", "13.0.3");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void delete_notFound_returns404() {
    RepoEntity repo = createRepo("nuget-hosted", RepositoryFormat.NUGET, RepositoryType.HOSTED);
    repositoryQuery.setRepo(repo);

    ResponseEntity<?> response = controller.delete("nuget-hosted", "Missing.Pkg", "1.0.0");

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_nonHosted_throwsException() {
    RepoEntity repo = createRepo("nuget-proxy", RepositoryFormat.NUGET, RepositoryType.PROXY);
    repositoryQuery.setRepo(repo);

    assertThatThrownBy(() -> controller.delete("nuget-proxy", "Newtonsoft.Json", "13.0.3"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("hosted");
  }
}
