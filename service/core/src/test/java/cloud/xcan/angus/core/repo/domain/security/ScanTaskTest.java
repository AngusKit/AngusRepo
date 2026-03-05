package cloud.xcan.angus.core.repo.domain.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ScanTaskTest {

    private ScanTask scanTask;

    @BeforeEach
    void setUp() {
        scanTask = new ScanTask();
    }

    @Test
    void testBasicProperties() {
        // Given
        String id = "scan-001";
        String artifactId = "artifact-001";
        String repositoryId = "repo-001";

        // When
        ScanTask result = scanTask.setId(id)
                .setArtifactId(artifactId)
                .setRepositoryId(repositoryId)
                .setScanType(ScanType.VULNERABILITY)
                .setStatus(ScanStatus.PENDING);

        // Then
        assertThat(result).isSameAs(scanTask);
        assertThat(scanTask.getId()).isEqualTo(id);
        assertThat(scanTask.getArtifactId()).isEqualTo(artifactId);
        assertThat(scanTask.getRepositoryId()).isEqualTo(repositoryId);
        assertThat(scanTask.getScanType()).isEqualTo(ScanType.VULNERABILITY);
        assertThat(scanTask.getStatus()).isEqualTo(ScanStatus.PENDING);
    }

    @Test
    void testIdentity() {
        // Given
        String id = "scan-001";

        // When
        scanTask.setId(id);

        // Then
        assertThat(scanTask.identity()).isEqualTo(id);
    }

    @Test
    void testCalculateDurationSeconds() {
        // Given
        LocalDateTime start = LocalDateTime.of(2024, 1, 15, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

        // When
        scanTask.setStartTime(start).setEndTime(end);

        // Then
        assertThat(scanTask.calculateDurationSeconds()).isEqualTo(1800L);
    }

    @Test
    void testCalculateDurationSeconds_NullTimes() {
        // When start and end are null
        assertThat(scanTask.calculateDurationSeconds()).isNull();

        // When only start is set
        scanTask.setStartTime(LocalDateTime.now());
        assertThat(scanTask.calculateDurationSeconds()).isNull();
    }

    @Test
    void testIsRunning() {
        // Given & When & Then
        scanTask.setStatus(ScanStatus.PENDING);
        assertThat(scanTask.isRunning()).isTrue();

        scanTask.setStatus(ScanStatus.SCANNING);
        assertThat(scanTask.isRunning()).isTrue();

        scanTask.setStatus(ScanStatus.COMPLETED);
        assertThat(scanTask.isRunning()).isFalse();

        scanTask.setStatus(ScanStatus.FAILED);
        assertThat(scanTask.isRunning()).isFalse();

        scanTask.setStatus(ScanStatus.CANCELLED);
        assertThat(scanTask.isRunning()).isFalse();
    }

    @Test
    void testIsFinished() {
        // Given & When & Then
        scanTask.setStatus(ScanStatus.COMPLETED);
        assertThat(scanTask.isFinished()).isTrue();

        scanTask.setStatus(ScanStatus.FAILED);
        assertThat(scanTask.isFinished()).isTrue();

        scanTask.setStatus(ScanStatus.CANCELLED);
        assertThat(scanTask.isFinished()).isTrue();

        scanTask.setStatus(ScanStatus.PENDING);
        assertThat(scanTask.isFinished()).isFalse();

        scanTask.setStatus(ScanStatus.SCANNING);
        assertThat(scanTask.isFinished()).isFalse();
    }

    @Test
    void testVulnerabilityCounts() {
        // Given & When
        scanTask.setVulnerabilityCount(10)
                .setCriticalCount(2)
                .setHighCount(3)
                .setMediumCount(3)
                .setLowCount(2);

        // Then
        assertThat(scanTask.getVulnerabilityCount()).isEqualTo(10);
        assertThat(scanTask.getCriticalCount()).isEqualTo(2);
        assertThat(scanTask.getHighCount()).isEqualTo(3);
        assertThat(scanTask.getMediumCount()).isEqualTo(3);
        assertThat(scanTask.getLowCount()).isEqualTo(2);
    }

    @Test
    void testTransientFields() {
        // Given
        String artifactName = "my-artifact";
        String repositoryName = "my-repo";

        // When
        scanTask.setArtifactName(artifactName)
                .setRepositoryName(repositoryName);

        // Then
        assertThat(scanTask.getArtifactName()).isEqualTo(artifactName);
        assertThat(scanTask.getRepositoryName()).isEqualTo(repositoryName);
    }

    @Test
    void testDefaultValues() {
        // Then
        assertThat(scanTask.getProgress()).isEqualTo(0);
        assertThat(scanTask.getVulnerabilityCount()).isEqualTo(0);
        assertThat(scanTask.getCriticalCount()).isEqualTo(0);
        assertThat(scanTask.getHighCount()).isEqualTo(0);
        assertThat(scanTask.getMediumCount()).isEqualTo(0);
        assertThat(scanTask.getLowCount()).isEqualTo(0);
    }
}
