package cloud.xcan.angus.core.repo.domain.upload;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * UploadStatus enum unit tests.
 */
public class UploadStatusTest {

    @Test
    void testValues() {
        // Given & When
        UploadStatus[] values = UploadStatus.values();

        // Then: all 6 values exist
        assertThat(values).containsExactlyInAnyOrder(
            UploadStatus.PENDING,
            UploadStatus.UPLOADING,
            UploadStatus.PROCESSING,
            UploadStatus.COMPLETED,
            UploadStatus.FAILED,
            UploadStatus.CANCELLED
        );
    }

    @Test
    void testIsActive() {
        // Given & When & Then: PENDING, UPLOADING, PROCESSING are active
        assertThat(UploadStatus.PENDING.isActive()).isTrue();
        assertThat(UploadStatus.UPLOADING.isActive()).isTrue();
        assertThat(UploadStatus.PROCESSING.isActive()).isTrue();

        // Non-active statuses
        assertThat(UploadStatus.COMPLETED.isActive()).isFalse();
        assertThat(UploadStatus.FAILED.isActive()).isFalse();
        assertThat(UploadStatus.CANCELLED.isActive()).isFalse();
    }

    @Test
    void testIsTerminal() {
        // Given & When & Then: COMPLETED, FAILED, CANCELLED are terminal
        assertThat(UploadStatus.COMPLETED.isTerminal()).isTrue();
        assertThat(UploadStatus.FAILED.isTerminal()).isTrue();
        assertThat(UploadStatus.CANCELLED.isTerminal()).isTrue();

        // Non-terminal statuses
        assertThat(UploadStatus.PENDING.isTerminal()).isFalse();
        assertThat(UploadStatus.UPLOADING.isTerminal()).isFalse();
        assertThat(UploadStatus.PROCESSING.isTerminal()).isFalse();
    }

    @Test
    void testValueMapping() {
        // Given & When & Then
        assertThat(UploadStatus.PENDING.getValue()).isEqualTo("pending");
        assertThat(UploadStatus.UPLOADING.getValue()).isEqualTo("uploading");
        assertThat(UploadStatus.PROCESSING.getValue()).isEqualTo("processing");
        assertThat(UploadStatus.COMPLETED.getValue()).isEqualTo("completed");
        assertThat(UploadStatus.FAILED.getValue()).isEqualTo("failed");
        assertThat(UploadStatus.CANCELLED.getValue()).isEqualTo("cancelled");
    }

    @Test
    void testCount() {
        // Given & When & Then: exactly 6 values
        assertThat(UploadStatus.values()).hasSize(6);
    }
}
