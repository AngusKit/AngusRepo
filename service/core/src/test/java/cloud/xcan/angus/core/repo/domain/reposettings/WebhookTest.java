package cloud.xcan.angus.core.repo.domain.reposettings;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * Webhook entity unit tests.
 */
public class WebhookTest {

    private Webhook webhook;

    @BeforeEach
    void setUp() {
        webhook = new Webhook();
    }

    @Test
    void testBasicProperties() {
        // Given
        Long id = 1L;
        String name = "CI Webhook";
        String url = "https://example.com/webhook";
        String secret = "secret-key-123";
        String events = "[\"artifact_upload\",\"artifact_delete\"]";
        LocalDateTime lastTrigger = LocalDateTime.now();

        // When
        webhook.setId(id)
               .setName(name)
               .setUrl(url)
               .setSecret(secret)
               .setEvents(events)
               .setLastTriggerTime(lastTrigger);

        // Then
        assertThat(webhook.getId()).isEqualTo(id);
        assertThat(webhook.getName()).isEqualTo(name);
        assertThat(webhook.getUrl()).isEqualTo(url);
        assertThat(webhook.getSecret()).isEqualTo(secret);
        assertThat(webhook.getEvents()).isEqualTo(events);
        assertThat(webhook.getLastTriggerTime()).isEqualTo(lastTrigger);
    }

    @Test
    void testDefaultValues() {
        // Given: a newly created webhook entity
        Webhook newWebhook = new Webhook();

        // Then: verify default values
        assertThat(newWebhook.getActive()).isTrue();
        assertThat(newWebhook.getSuccessCount()).isEqualTo(0);
        assertThat(newWebhook.getFailureCount()).isEqualTo(0);
    }

    @Test
    void testAuditFields() {
        // Given
        Long createdBy = 1001L;
        Long modifiedBy = 1002L;
        LocalDateTime now = LocalDateTime.now();

        // When
        webhook.setCreatedBy(createdBy)
               .setCreatedDate(now)
               .setModifiedBy(modifiedBy)
               .setModifiedDate(now);

        // Then
        assertThat(webhook.getCreatedBy()).isEqualTo(createdBy);
        assertThat(webhook.getCreatedDate()).isEqualTo(now);
        assertThat(webhook.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(webhook.getModifiedDate()).isEqualTo(now);
    }

    @Test
    void testChainedSetters() {
        // Given
        Long id = 1L;
        String name = "Deploy Webhook";
        String url = "https://example.com/deploy";

        // When
        Webhook result = webhook.setId(id)
                                .setName(name)
                                .setUrl(url)
                                .setActive(false);

        // Then: chained calls return the same instance
        assertThat(result).isSameAs(webhook);
        assertThat(webhook.getId()).isEqualTo(id);
        assertThat(webhook.getName()).isEqualTo(name);
        assertThat(webhook.getUrl()).isEqualTo(url);
        assertThat(webhook.getActive()).isFalse();
    }

    @Test
    void testIdentityMethod() {
        // Given
        Long id = 42L;
        webhook.setId(id);

        // When & Then
        assertThat(webhook.identity()).isEqualTo(id);
    }

    @Test
    void testIdentityMethodWithNullId() {
        // Given
        webhook.setId(null);

        // When & Then
        assertThat(webhook.identity()).isNull();
    }
}
