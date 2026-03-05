package cloud.xcan.angus.core.repo.domain.artifact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Artifact entity unit tests.
 */
public class ArtifactTest {

    private Artifact artifact;

    @BeforeEach
    void setUp() {
        artifact = new Artifact();
    }

    @Test
    void testBasicProperties() {
        // Given
        Long id = 1L;
        Long repositoryId = 100L;
        String name = "my-artifact";
        String path = "/com/example/my-artifact";
        String version = "1.0.0";
        String description = "A test artifact";

        // When
        artifact.setId(id)
               .setRepositoryId(repositoryId)
               .setName(name)
               .setPath(path)
               .setVersion(version)
               .setDescription(description);

        // Then
        assertThat(artifact.getId()).isEqualTo(id);
        assertThat(artifact.getRepositoryId()).isEqualTo(repositoryId);
        assertThat(artifact.getName()).isEqualTo(name);
        assertThat(artifact.getPath()).isEqualTo(path);
        assertThat(artifact.getVersion()).isEqualTo(version);
        assertThat(artifact.getDescription()).isEqualTo(description);
    }

    @Test
    void testDefaultValues() {
        // Given: a newly created artifact entity
        Artifact newArtifact = new Artifact();

        // Then: verify default values
        assertThat(newArtifact.getDownloads()).isEqualTo(0);
        assertThat(newArtifact.getStars()).isEqualTo(0);
        assertThat(newArtifact.getIsLatest()).isFalse();
        assertThat(newArtifact.getSizeBytes()).isEqualTo(0L);
    }

    @Test
    void testAuditFields() {
        // Given
        Long createdBy = 1001L;
        Long modifiedBy = 1002L;
        LocalDateTime now = LocalDateTime.now();

        // When
        artifact.setCreatedBy(createdBy)
               .setCreatedDate(now)
               .setModifiedBy(modifiedBy)
               .setModifiedDate(now);

        // Then
        assertThat(artifact.getCreatedBy()).isEqualTo(createdBy);
        assertThat(artifact.getCreatedDate()).isEqualTo(now);
        assertThat(artifact.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(artifact.getModifiedDate()).isEqualTo(now);
    }

    @Test
    void testTransientFields() {
        // Given
        String repositoryName = "Test Repository";
        ArtifactFormat format = ArtifactFormat.MAVEN;
        List<String> parsedTags = Arrays.asList("stable", "release");
        ArtifactVulnerability parsedVulnerability = new ArtifactVulnerability(1, 2, 3, 4, null);
        ArtifactMetadata parsedMetadata = new ArtifactMetadata();
        parsedMetadata.setGroupId("com.example");

        // When
        artifact.setRepositoryName(repositoryName)
               .setFormat(format)
               .setParsedTags(parsedTags)
               .setParsedVulnerability(parsedVulnerability)
               .setParsedMetadata(parsedMetadata);

        // Then
        assertThat(artifact.getRepositoryName()).isEqualTo(repositoryName);
        assertThat(artifact.getFormat()).isEqualTo(format);
        assertThat(artifact.getParsedTags()).containsExactly("stable", "release");
        assertThat(artifact.getParsedVulnerability()).isEqualTo(parsedVulnerability);
        assertThat(artifact.getParsedMetadata()).isEqualTo(parsedMetadata);
    }

    @Test
    void testChainedSetters() {
        // Given
        Long id = 1L;
        String name = "my-artifact";
        String version = "2.0.0";

        // When
        Artifact result = artifact.setId(id)
                                  .setName(name)
                                  .setVersion(version)
                                  .setIsLatest(true);

        // Then: chained calls return the same instance
        assertThat(result).isSameAs(artifact);
        assertThat(artifact.getId()).isEqualTo(id);
        assertThat(artifact.getName()).isEqualTo(name);
        assertThat(artifact.getVersion()).isEqualTo(version);
        assertThat(artifact.getIsLatest()).isTrue();
    }

    @Test
    void testIdentityMethod() {
        // Given
        Long id = 42L;
        artifact.setId(id);

        // When & Then
        assertThat(artifact.identity()).isEqualTo(id);
    }

    @Test
    void testIdentityMethodWithNullId() {
        // Given: ID is null
        artifact.setId(null);

        // When & Then
        assertThat(artifact.identity()).isNull();
    }

    @Test
    void testNullValues() {
        // When: set null values
        artifact.setDescription(null)
               .setPath(null)
               .setVersion(null)
               .setChecksum(null)
               .setLicense(null)
               .setRepositoryName(null)
               .setFormat(null)
               .setParsedTags(null)
               .setParsedVulnerability(null)
               .setParsedMetadata(null);

        // Then: verify null handling
        assertThat(artifact.getDescription()).isNull();
        assertThat(artifact.getPath()).isNull();
        assertThat(artifact.getVersion()).isNull();
        assertThat(artifact.getChecksum()).isNull();
        assertThat(artifact.getLicense()).isNull();
        assertThat(artifact.getRepositoryName()).isNull();
        assertThat(artifact.getFormat()).isNull();
        assertThat(artifact.getParsedTags()).isNull();
        assertThat(artifact.getParsedVulnerability()).isNull();
        assertThat(artifact.getParsedMetadata()).isNull();
    }
}
