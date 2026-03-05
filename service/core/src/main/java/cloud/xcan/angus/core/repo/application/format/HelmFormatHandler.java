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
 * Helm Chart repository format handler.
 * Implements Helm Chart Repository API and ChartMuseum API compatibility.
 * Supports hosted/proxy/group repository types for helm CLI (helm repo add/install/push).
 */
@Component
public class HelmFormatHandler implements ArtifactFormatHandler {

  private static final String FORMAT_NAME = "Helm";

  @Override
  public RepositoryFormat getFormat() {
    return RepositoryFormat.HELM;
  }

  @Override
  public ArtifactMetadata parseMetadata(InputStream inputStream, String fileName) {
    ArtifactMetadata metadata = new ArtifactMetadata();
    metadata.setPackaging("helm");
    // Chart.yaml would be parsed from the .tgz archive
    return metadata;
  }

  @Override
  public ValidationResult validateArtifact(InputStream inputStream, String fileName) {
    if (fileName == null || fileName.trim().isEmpty()) {
      return ValidationResult.failure("Chart file name is required");
    }
    if (!fileName.toLowerCase().endsWith(".tgz") && !fileName.toLowerCase().endsWith(".tar.gz")) {
      return ValidationResult.failure("Invalid Helm chart format. Expected .tgz file");
    }
    return ValidationResult.success();
  }

  @Override
  public String generateStoragePath(ArtifactMetadata metadata) {
    return "helm/charts/";
  }

  @Override
  public String generateAccessUrl(RepoEntity repository, ArtifactMetadata metadata) {
    String baseUrl = repository.getUrl() != null ? repository.getUrl() : "";
    return baseUrl + "/charts/";
  }

  @Override
  public ResponseEntity<?> handleFormatSpecificRequest(HttpServletRequest request,
      RepoEntity repository) {
    String path = request.getRequestURI();
    if (path.endsWith("/index.yaml")) {
      return ResponseEntity.ok().header("Content-Type", "application/x-yaml").build();
    }
    return ResponseEntity.notFound().build();
  }

  @Override
  public SetupGuide generateSetupGuide(RepoEntity repository, String authToken) {
    String repoUrl = repository.getUrl() != null ? repository.getUrl() : "https://repo.example.com/repository/" + repository.getName();
    String config = String.format(
        "# Add Helm repository\n"
        + "helm repo add %s %s \\\n"
        + "  --username {username} \\\n"
        + "  --password %s\n\n"
        + "# Update repo index\n"
        + "helm repo update\n\n"
        + "# Install chart\n"
        + "helm install my-release %s/chart-name\n\n"
        + "# Push chart (requires helm-push plugin)\n"
        + "helm cm-push my-chart.tgz %s",
        repository.getName(), repoUrl, authToken != null ? authToken : "{token}",
        repository.getName(), repository.getName());

    SetupGuide guide = new SetupGuide(FORMAT_NAME, repoUrl, config);
    guide.addInstruction("1. Add repository", "Use 'helm repo add' to register this chart repository");
    guide.addInstruction("2. Install charts", "Use 'helm install' to deploy charts from this repository");
    guide.addInstruction("3. Push charts", "Use 'helm cm-push' with the helm-push plugin to upload charts");
    return guide;
  }

  @Override
  public byte[] generateIndex(RepoEntity repository) {
    // Helm index.yaml
    String indexYaml = "apiVersion: v1\n"
        + "entries: {}\n"
        + "generated: \"" + java.time.Instant.now().toString() + "\"\n";
    return indexYaml.getBytes();
  }
}
