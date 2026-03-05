package cloud.xcan.angus.core.repo.domain.upload;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * UploadTask entity unit tests.
 */
public class UploadTaskTest {

    private UploadTask task;

    @BeforeEach
    void setUp() {
        task = new UploadTask();
    }

    @Test
    void testBasicProperties() {
        // Given
        Long id = 1L;
        Long repositoryId = 100L;
        String fileName = "my-artifact-1.0.jar";
        Long fileSize = 1024L;
        String checksum = "sha256:abc123";
        String path = "/com/example";
        String version = "1.0.0";
        String uploadToken = "token-001";
        Long artifactId = 200L;
        String errorMessage = "Upload failed";

        // When
        task.setId(id)
            .setRepositoryId(repositoryId)
            .setFileName(fileName)
            .setFileSize(fileSize)
            .setChecksum(checksum)
            .setPath(path)
            .setVersion(version)
            .setUploadToken(uploadToken)
            .setArtifactId(artifactId)
            .setErrorMessage(errorMessage);

        // Then
        assertThat(task.getId()).isEqualTo(id);
        assertThat(task.getRepositoryId()).isEqualTo(repositoryId);
        assertThat(task.getFileName()).isEqualTo(fileName);
        assertThat(task.getFileSize()).isEqualTo(fileSize);
        assertThat(task.getChecksum()).isEqualTo(checksum);
        assertThat(task.getPath()).isEqualTo(path);
        assertThat(task.getVersion()).isEqualTo(version);
        assertThat(task.getUploadToken()).isEqualTo(uploadToken);
        assertThat(task.getArtifactId()).isEqualTo(artifactId);
        assertThat(task.getErrorMessage()).isEqualTo(errorMessage);
    }

    @Test
    void testDefaultValues() {
        // Given: a newly created upload task entity
        UploadTask newTask = new UploadTask();

        // Then: verify default values
        assertThat(newTask.getStatus()).isEqualTo(UploadStatus.PENDING);
        assertThat(newTask.getEnableChunked()).isFalse();
        assertThat(newTask.getTotalChunks()).isEqualTo(0);
        assertThat(newTask.getUploadedChunks()).isEqualTo(0);
        assertThat(newTask.getProgress()).isEqualTo(0);
    }

    @Test
    void testGetProgressPercentWithChunks() {
        // Given
        task.setTotalChunks(10)
            .setUploadedChunks(5);

        // When
        int percent = task.getProgressPercent();

        // Then
        assertThat(percent).isEqualTo(50);
    }

    @Test
    void testGetProgressPercentWithZeroTotalChunks() {
        // Given
        task.setTotalChunks(0)
            .setProgress(75);

        // When
        int percent = task.getProgressPercent();

        // Then: falls back to progress field
        assertThat(percent).isEqualTo(75);
    }

    @Test
    void testGetProgressPercentWithNullTotalChunks() {
        // Given
        task.setTotalChunks(null)
            .setProgress(60);

        // When
        int percent = task.getProgressPercent();

        // Then: falls back to progress field
        assertThat(percent).isEqualTo(60);
    }

    @Test
    void testGetProgressPercentAllChunksUploaded() {
        // Given
        task.setTotalChunks(4)
            .setUploadedChunks(4);

        // When
        int percent = task.getProgressPercent();

        // Then
        assertThat(percent).isEqualTo(100);
    }

    @Test
    void testIsExpiredWhenExpired() {
        // Given
        task.setExpires(LocalDateTime.now().minusHours(1));

        // When & Then
        assertThat(task.isExpired()).isTrue();
    }

    @Test
    void testIsExpiredWhenNotExpired() {
        // Given
        task.setExpires(LocalDateTime.now().plusHours(1));

        // When & Then
        assertThat(task.isExpired()).isFalse();
    }

    @Test
    void testIsExpiredWhenNull() {
        // Given
        task.setExpires(null);

        // When & Then
        assertThat(task.isExpired()).isFalse();
    }

    @Test
    void testIsActiveForPending() {
        // Given
        task.setStatus(UploadStatus.PENDING);

        // When & Then
        assertThat(task.isActive()).isTrue();
    }

    @Test
    void testIsActiveForUploading() {
        // Given
        task.setStatus(UploadStatus.UPLOADING);

        // When & Then
        assertThat(task.isActive()).isTrue();
    }

    @Test
    void testIsActiveForProcessing() {
        // Given
        task.setStatus(UploadStatus.PROCESSING);

        // When & Then
        assertThat(task.isActive()).isTrue();
    }

    @Test
    void testIsActiveForCompleted() {
        // Given
        task.setStatus(UploadStatus.COMPLETED);

        // When & Then
        assertThat(task.isActive()).isFalse();
    }

    @Test
    void testIsTerminalForCompleted() {
        // Given
        task.setStatus(UploadStatus.COMPLETED);

        // When & Then
        assertThat(task.isTerminal()).isTrue();
    }

    @Test
    void testIsTerminalForFailed() {
        // Given
        task.setStatus(UploadStatus.FAILED);

        // When & Then
        assertThat(task.isTerminal()).isTrue();
    }

    @Test
    void testIsTerminalForCancelled() {
        // Given
        task.setStatus(UploadStatus.CANCELLED);

        // When & Then
        assertThat(task.isTerminal()).isTrue();
    }

    @Test
    void testIsTerminalForPending() {
        // Given
        task.setStatus(UploadStatus.PENDING);

        // When & Then
        assertThat(task.isTerminal()).isFalse();
    }

    @Test
    void testChainedSetters() {
        // Given
        Long id = 1L;
        String fileName = "test.jar";
        UploadStatus status = UploadStatus.UPLOADING;

        // When
        UploadTask result = task.setId(id)
                                .setFileName(fileName)
                                .setStatus(status);

        // Then: chained calls return the same instance
        assertThat(result).isSameAs(task);
        assertThat(task.getId()).isEqualTo(id);
        assertThat(task.getFileName()).isEqualTo(fileName);
        assertThat(task.getStatus()).isEqualTo(status);
    }

    @Test
    void testIdentityMethod() {
        // Given
        Long id = 42L;
        task.setId(id);

        // When & Then
        assertThat(task.identity()).isEqualTo(id);
    }

    @Test
    void testIdentityMethodWithNullId() {
        // Given
        task.setId(null);

        // When & Then
        assertThat(task.identity()).isNull();
    }
}
