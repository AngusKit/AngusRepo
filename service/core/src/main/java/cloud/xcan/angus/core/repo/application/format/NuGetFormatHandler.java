package cloud.xcan.angus.core.repo.application.format;

import cloud.xcan.angus.core.repo.domain.artifact.ArtifactMetadata;
import cloud.xcan.angus.core.repo.domain.format.ArtifactFormatHandler;
import cloud.xcan.angus.core.repo.domain.format.SetupGuide;
import cloud.xcan.angus.core.repo.domain.format.ValidationResult;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import java.io.InputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * NuGet V3 API format handler.
 * Implements NuGet V3 Server API compatibility for dotnet CLI/Visual Studio/NuGet CLI.
 * Supports hosted/proxy/group repository types with Service Index discovery.
 */
@Component
public class NuGetFormatHandler implements ArtifactFormatHandler {

  private static final String FORMAT_NAME = "NuGet";

  @Override
  public RepositoryFormat getFormat() {
    return RepositoryFormat.NUGET;
  }

  @Override
  public ArtifactMetadata parseMetadata(InputStream inputStream, String fileName) {
    ArtifactMetadata metadata = new ArtifactMetadata();
    metadata.setPackaging("nupkg");
    return metadata;
  }

  @Override
  public ValidationResult validateArtifact(InputStream inputStream, String fileName) {
    if (fileName == null || fileName.trim().isEmpty()) {
      return ValidationResult.failure("Package file name is required");
    }
    if (!fileName.toLowerCase().endsWith(".nupkg") && !fileName.toLowerCase().endsWith(".snupkg")) {
      return ValidationResult.failure("Invalid NuGet package format. Expected .nupkg or .snupkg file");
    }
    return ValidationResult.success();
  }

  @Override
  public String generateStoragePath(ArtifactMetadata metadata) {
    StringBuilder path = new StringBuilder("nuget/");
    if (metadata.getArtifactId() != null) {
      path.append(metadata.getArtifactId().toLowerCase()).append("/");
    }
    return path.toString();
  }

  @Override
  public String generateAccessUrl(RepoEntity repository, ArtifactMetadata metadata) {
    String baseUrl = repository.getUrl() != null ? repository.getUrl() : "";
    return baseUrl + "/v3/flatcontainer/" + (metadata.getArtifactId() != null ? metadata.getArtifactId().toLowerCase() : "");
  }

  @Override
  public ResponseEntity<?> handleFormatSpecificRequest(HttpServletRequest request,
      RepoEntity repository) {
    String path = request.getRequestURI();
    // NuGet V3 Service Index
    if (path.endsWith("/v3/index.json")) {
      String baseUrl = repository.getUrl() != null ? repository.getUrl() : "";
      String serviceIndex = String.format(
          "{\"version\":\"3.0.0\",\"resources\":["
          + "{\"@id\":\"%s/v3/search\",\"@type\":\"SearchQueryService\"},"
          + "{\"@id\":\"%s/v3/registration/\",\"@type\":\"RegistrationsBaseUrl\"},"
          + "{\"@id\":\"%s/v3/flatcontainer/\",\"@type\":\"PackageBaseAddress/3.0.0\"},"
          + "{\"@id\":\"%s/api/v2/package\",\"@type\":\"PackagePublish/2.0.0\"}"
          + "]}", baseUrl, baseUrl, baseUrl, baseUrl);
      return ResponseEntity.ok().header("Content-Type", "application/json").body(serviceIndex);
    }
    return ResponseEntity.notFound().build();
  }

  @Override
  public SetupGuide generateSetupGuide(RepoEntity repository, String authToken) {
    String repoUrl = repository.getUrl() != null ? repository.getUrl() : "https://repo.example.com/repository/" + repository.getName();
    String config = String.format(
        "# Add NuGet source\n"
        + "dotnet nuget add source %s/v3/index.json \\\n"
        + "  --name %s \\\n"
        + "  --username {username} \\\n"
        + "  --password %s\n\n"
        + "# Push package\n"
        + "dotnet nuget push my-package.nupkg \\\n"
        + "  --source %s \\\n"
        + "  --api-key %s",
        repoUrl, repository.getName(), authToken != null ? authToken : "{token}",
        repository.getName(), authToken != null ? authToken : "{api-key}");

    SetupGuide guide = new SetupGuide(FORMAT_NAME, repoUrl, config);
    guide.addInstruction("1. Add NuGet source", "Use dotnet CLI to add this repository as a NuGet source");
    guide.addInstruction("2. Push packages", "Use 'dotnet nuget push' with your API key");
    guide.addInstruction("3. Restore packages", "Use 'dotnet restore' to download packages from this source");
    return guide;
  }

  @Override
  public byte[] generateIndex(RepoEntity repository) {
    return "{\"version\":\"3.0.0\",\"resources\":[]}".getBytes();
  }
}
