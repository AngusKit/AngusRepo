package cloud.xcan.angus.core.repo.domain.artifact;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * ArtifactMetadata and ArtifactVulnerability value object unit tests.
 */
public class ArtifactValueObjectsTest {

    // ---- ArtifactMetadata tests ----

    @Test
    void testMetadataBasicProperties() {
        // Given
        ArtifactMetadata metadata = new ArtifactMetadata();

        // When
        metadata.setGroupId("com.example");
        metadata.setArtifactId("my-lib");
        metadata.setPackaging("jar");
        metadata.setDigest("sha256:abc123");
        metadata.setLayers(5);
        metadata.setArchitecture("amd64");
        metadata.setOs("linux");
        metadata.setScope("compile");
        metadata.setDependencies("dep1,dep2");
        metadata.setPythonVersion("3.11");
        metadata.setClassifiers("py3-none-any");
        metadata.setAuthors("John Doe");
        metadata.setProjectUrl("https://example.com");

        // Then
        assertThat(metadata.getGroupId()).isEqualTo("com.example");
        assertThat(metadata.getArtifactId()).isEqualTo("my-lib");
        assertThat(metadata.getPackaging()).isEqualTo("jar");
        assertThat(metadata.getDigest()).isEqualTo("sha256:abc123");
        assertThat(metadata.getLayers()).isEqualTo(5);
        assertThat(metadata.getArchitecture()).isEqualTo("amd64");
        assertThat(metadata.getOs()).isEqualTo("linux");
        assertThat(metadata.getScope()).isEqualTo("compile");
        assertThat(metadata.getDependencies()).isEqualTo("dep1,dep2");
        assertThat(metadata.getPythonVersion()).isEqualTo("3.11");
        assertThat(metadata.getClassifiers()).isEqualTo("py3-none-any");
        assertThat(metadata.getAuthors()).isEqualTo("John Doe");
        assertThat(metadata.getProjectUrl()).isEqualTo("https://example.com");
    }

    @Test
    void testMetadataDefaultConstructor() {
        // Given & When
        ArtifactMetadata metadata = new ArtifactMetadata();

        // Then
        assertThat(metadata.getGroupId()).isNull();
        assertThat(metadata.getArtifactId()).isNull();
        assertThat(metadata.getPackaging()).isNull();
        assertThat(metadata.getDigest()).isNull();
        assertThat(metadata.getLayers()).isNull();
    }

    @Test
    void testMetadataAllArgsConstructor() {
        // Given & When
        ArtifactMetadata metadata = new ArtifactMetadata(
            "com.example", "my-lib", "jar", "sha256:abc123", 5,
            "amd64", "linux", "compile", "dep1", "3.11",
            "py3-none-any", "Author", "https://example.com"
        );

        // Then
        assertThat(metadata.getGroupId()).isEqualTo("com.example");
        assertThat(metadata.getArtifactId()).isEqualTo("my-lib");
        assertThat(metadata.getPackaging()).isEqualTo("jar");
        assertThat(metadata.getDigest()).isEqualTo("sha256:abc123");
        assertThat(metadata.getLayers()).isEqualTo(5);
        assertThat(metadata.getArchitecture()).isEqualTo("amd64");
        assertThat(metadata.getOs()).isEqualTo("linux");
        assertThat(metadata.getScope()).isEqualTo("compile");
        assertThat(metadata.getDependencies()).isEqualTo("dep1");
        assertThat(metadata.getPythonVersion()).isEqualTo("3.11");
        assertThat(metadata.getClassifiers()).isEqualTo("py3-none-any");
        assertThat(metadata.getAuthors()).isEqualTo("Author");
        assertThat(metadata.getProjectUrl()).isEqualTo("https://example.com");
    }

    @Test
    void testMetadataIsValidWithGroupId() {
        // Given
        ArtifactMetadata metadata = new ArtifactMetadata();
        metadata.setGroupId("com.example");

        // When & Then
        assertThat(metadata.isValid()).isTrue();
    }

    @Test
    void testMetadataIsValidWithArtifactId() {
        // Given
        ArtifactMetadata metadata = new ArtifactMetadata();
        metadata.setArtifactId("my-lib");

        // When & Then
        assertThat(metadata.isValid()).isTrue();
    }

    @Test
    void testMetadataIsValidWithDigest() {
        // Given
        ArtifactMetadata metadata = new ArtifactMetadata();
        metadata.setDigest("sha256:abc123");

        // When & Then
        assertThat(metadata.isValid()).isTrue();
    }

    @Test
    void testMetadataIsNotValid() {
        // Given
        ArtifactMetadata metadata = new ArtifactMetadata();

        // When & Then
        assertThat(metadata.isValid()).isFalse();
    }

    // ---- ArtifactVulnerability tests ----

    @Test
    void testVulnerabilityBasicProperties() {
        // Given
        ArtifactVulnerability vuln = new ArtifactVulnerability();

        // When
        vuln.setCritical(2);
        vuln.setHigh(5);
        vuln.setMedium(10);
        vuln.setLow(20);

        // Then
        assertThat(vuln.getCritical()).isEqualTo(2);
        assertThat(vuln.getHigh()).isEqualTo(5);
        assertThat(vuln.getMedium()).isEqualTo(10);
        assertThat(vuln.getLow()).isEqualTo(20);
    }

    @Test
    void testVulnerabilityDefaultValues() {
        // Given & When
        ArtifactVulnerability vuln = new ArtifactVulnerability();

        // Then
        assertThat(vuln.getCritical()).isEqualTo(0);
        assertThat(vuln.getHigh()).isEqualTo(0);
        assertThat(vuln.getMedium()).isEqualTo(0);
        assertThat(vuln.getLow()).isEqualTo(0);
        assertThat(vuln.getLastScanDate()).isNull();
    }

    @Test
    void testVulnerabilityGetTotalCount() {
        // Given
        ArtifactVulnerability vuln = new ArtifactVulnerability(1, 2, 3, 4, null);

        // When
        int total = vuln.getTotalCount();

        // Then
        assertThat(total).isEqualTo(10);
    }

    @Test
    void testVulnerabilityGetTotalCountWithNulls() {
        // Given
        ArtifactVulnerability vuln = new ArtifactVulnerability();
        vuln.setCritical(null);
        vuln.setHigh(null);
        vuln.setMedium(3);
        vuln.setLow(null);

        // When
        int total = vuln.getTotalCount();

        // Then
        assertThat(total).isEqualTo(3);
    }

    @Test
    void testVulnerabilityHasCritical() {
        // Given
        ArtifactVulnerability vuln = new ArtifactVulnerability();
        vuln.setCritical(1);

        // When & Then
        assertThat(vuln.hasCritical()).isTrue();
    }

    @Test
    void testVulnerabilityHasNoCritical() {
        // Given
        ArtifactVulnerability vuln = new ArtifactVulnerability();

        // When & Then
        assertThat(vuln.hasCritical()).isFalse();
    }

    @Test
    void testVulnerabilityHasCriticalWithNull() {
        // Given
        ArtifactVulnerability vuln = new ArtifactVulnerability();
        vuln.setCritical(null);

        // When & Then
        assertThat(vuln.hasCritical()).isFalse();
    }

    @Test
    void testVulnerabilityAllArgsConstructor() {
        // Given
        LocalDateTime scanDate = LocalDateTime.now();

        // When
        ArtifactVulnerability vuln = new ArtifactVulnerability(5, 10, 15, 20, scanDate);

        // Then
        assertThat(vuln.getCritical()).isEqualTo(5);
        assertThat(vuln.getHigh()).isEqualTo(10);
        assertThat(vuln.getMedium()).isEqualTo(15);
        assertThat(vuln.getLow()).isEqualTo(20);
        assertThat(vuln.getLastScanDate()).isEqualTo(scanDate);
    }
}
