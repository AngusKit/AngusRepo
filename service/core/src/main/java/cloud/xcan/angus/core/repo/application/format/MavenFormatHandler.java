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
 * Maven repository format handler.
 * Implements Maven repository protocol compatibility for hosted/proxy/group repository types.
 * Supports .jar/.pom/.war/.ear/.aar files, maven-metadata.xml, and checksum files.
 */
@Component
public class MavenFormatHandler implements ArtifactFormatHandler {

  private static final String FORMAT_NAME = "Maven";
  private static final String PATH_PREFIX = "maven2";
  private static final String DEFAULT_PACKAGING = "jar";

  @Override
  public RepositoryFormat getFormat() {
    return RepositoryFormat.MAVEN;
  }

  @Override
  public ArtifactMetadata parseMetadata(InputStream inputStream, String fileName) {
    ArtifactMetadata metadata = new ArtifactMetadata();
    if (fileName != null && fileName.endsWith(".pom")) {
      metadata.setPackaging("pom");
    } else if (fileName != null && fileName.endsWith(".jar")) {
      metadata.setPackaging(DEFAULT_PACKAGING);
    } else if (fileName != null && fileName.endsWith(".war")) {
      metadata.setPackaging("war");
    } else {
      metadata.setPackaging(DEFAULT_PACKAGING);
    }
    // GAV coordinates would be parsed from the POM file or file path
    return metadata;
  }

  @Override
  public ValidationResult validateArtifact(InputStream inputStream, String fileName) {
    if (fileName == null || fileName.trim().isEmpty()) {
      return ValidationResult.failure("File name is required");
    }
    String lowerName = fileName.toLowerCase();
    if (lowerName.endsWith(".jar") || lowerName.endsWith(".pom")
        || lowerName.endsWith(".war") || lowerName.endsWith(".ear")
        || lowerName.endsWith(".aar") || lowerName.endsWith(".xml")
        || lowerName.endsWith(".sha1") || lowerName.endsWith(".md5")
        || lowerName.endsWith(".sha256") || lowerName.endsWith(".sha512")) {
      return ValidationResult.success();
    }
    return ValidationResult.failure("Unsupported Maven artifact type: " + fileName);
  }

  @Override
  public String generateStoragePath(ArtifactMetadata metadata) {
    StringBuilder path = new StringBuilder(PATH_PREFIX).append("/");
    if (metadata.getGroupId() != null) {
      path.append(metadata.getGroupId().replace('.', '/')).append("/");
    }
    if (metadata.getArtifactId() != null) {
      path.append(metadata.getArtifactId()).append("/");
    }
    return path.toString();
  }

  @Override
  public String generateAccessUrl(RepoEntity repository, ArtifactMetadata metadata) {
    String baseUrl = repository.getUrl() != null ? repository.getUrl() : "";
    return baseUrl + "/" + generateStoragePath(metadata);
  }

  @Override
  public ResponseEntity<?> handleFormatSpecificRequest(HttpServletRequest request,
      RepoEntity repository) {
    String path = request.getRequestURI();
    if (path.endsWith("maven-metadata.xml")) {
      return ResponseEntity.ok().header("Content-Type", "application/xml").build();
    }
    return ResponseEntity.notFound().build();
  }

  @Override
  public SetupGuide generateSetupGuide(RepoEntity repository, String authToken) {
    String repoUrl = repository.getUrl() != null ? repository.getUrl() : "https://repo.example.com/repository/" + repository.getName();
    String config = String.format(
        "<server>\n"
        + "    <id>%s</id>\n"
        + "    <username>{username}</username>\n"
        + "    <password>%s</password>\n"
        + "</server>\n"
        + "<repository>\n"
        + "    <id>%s</id>\n"
        + "    <url>%s</url>\n"
        + "</repository>",
        repository.getName(), authToken != null ? authToken : "{token}",
        repository.getName(), repoUrl);

    SetupGuide guide = new SetupGuide(FORMAT_NAME, repoUrl, config);
    guide.addInstruction("1. Configure settings.xml", "Add the server and repository configuration to your Maven settings.xml");
    guide.addInstruction("2. Deploy artifacts", "Use 'mvn deploy' to publish artifacts to this repository");
    guide.addInstruction("3. Resolve dependencies", "Add the repository to your pom.xml to resolve dependencies");
    return guide;
  }

  @Override
  public byte[] generateIndex(RepoEntity repository) {
    // Generate maven-metadata.xml for the repository
    String metadataXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<metadata>\n"
        + "  <groupId></groupId>\n"
        + "  <artifactId></artifactId>\n"
        + "  <versioning>\n"
        + "    <versions>\n"
        + "    </versions>\n"
        + "    <lastUpdated>" + System.currentTimeMillis() + "</lastUpdated>\n"
        + "  </versioning>\n"
        + "</metadata>";
    return metadataXml.getBytes();
  }
}
