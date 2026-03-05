package cloud.xcan.angus.core.repo.domain.reposettings;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * WebhookEvent enum unit tests.
 */
public class WebhookEventTest {

    @Test
    void testValues() {
        // Given & When
        WebhookEvent[] values = WebhookEvent.values();

        // Then: all 7 values exist
        assertThat(values).containsExactlyInAnyOrder(
            WebhookEvent.ARTIFACT_UPLOAD,
            WebhookEvent.ARTIFACT_DOWNLOAD,
            WebhookEvent.ARTIFACT_DELETE,
            WebhookEvent.SCAN_COMPLETE,
            WebhookEvent.VULNERABILITY_FOUND,
            WebhookEvent.REPOSITORY_CREATE,
            WebhookEvent.REPOSITORY_DELETE
        );
    }

    @Test
    void testValueMapping() {
        // Given & When & Then
        assertThat(WebhookEvent.ARTIFACT_UPLOAD.getValue()).isEqualTo("artifact_upload");
        assertThat(WebhookEvent.ARTIFACT_DOWNLOAD.getValue()).isEqualTo("artifact_download");
        assertThat(WebhookEvent.ARTIFACT_DELETE.getValue()).isEqualTo("artifact_delete");
        assertThat(WebhookEvent.SCAN_COMPLETE.getValue()).isEqualTo("scan_complete");
        assertThat(WebhookEvent.VULNERABILITY_FOUND.getValue()).isEqualTo("vulnerability_found");
        assertThat(WebhookEvent.REPOSITORY_CREATE.getValue()).isEqualTo("repository_create");
        assertThat(WebhookEvent.REPOSITORY_DELETE.getValue()).isEqualTo("repository_delete");
    }

    @Test
    void testCount() {
        // Given & When & Then: exactly 7 values
        assertThat(WebhookEvent.values()).hasSize(7);
    }
}
