package cloud.xcan.angus.core.repo.domain.notification;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NotificationTest {

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = new Notification();
    }

    @Test
    void testBasicProperties() {
        // Given
        String id = "notif-001";
        String title = "安全警告";
        String message = "发现新漏洞";

        // When
        Notification result = notification.setId(id)
                .setTitle(title)
                .setMessage(message)
                .setType(NotificationType.SECURITY)
                .setPriority(NotificationPriority.HIGH)
                .setTargetUserId(1001L);

        // Then
        assertThat(result).isSameAs(notification);
        assertThat(notification.getId()).isEqualTo(id);
        assertThat(notification.getTitle()).isEqualTo(title);
        assertThat(notification.getMessage()).isEqualTo(message);
        assertThat(notification.getType()).isEqualTo(NotificationType.SECURITY);
        assertThat(notification.getPriority()).isEqualTo(NotificationPriority.HIGH);
        assertThat(notification.getTargetUserId()).isEqualTo(1001L);
    }

    @Test
    void testIdentity() {
        // Given
        String id = "notif-001";

        // When
        notification.setId(id);

        // Then
        assertThat(notification.identity()).isEqualTo(id);
    }

    @Test
    void testDefaultValues() {
        // Then
        assertThat(notification.getIsRead()).isFalse();
        assertThat(notification.getIsStarred()).isFalse();
        assertThat(notification.getIsArchived()).isFalse();
        assertThat(notification.getPriority()).isEqualTo(NotificationPriority.MEDIUM);
    }

    @Test
    void testReadStatus() {
        // When
        notification.setIsRead(true);
        notification.setReadDate(LocalDateTime.now());

        // Then
        assertThat(notification.getIsRead()).isTrue();
        assertThat(notification.getReadDate()).isNotNull();
    }

    @Test
    void testStarAndArchive() {
        // When
        notification.setIsStarred(true).setIsArchived(true);

        // Then
        assertThat(notification.getIsStarred()).isTrue();
        assertThat(notification.getIsArchived()).isTrue();
    }

    @Test
    void testSourceInfo() {
        // Given
        String sourceId = "artifact-001";
        String sourceType = "ARTIFACT";
        String actionUrl = "/api/v1/artifacts/artifact-001";

        // When
        notification.setSourceId(sourceId)
                .setSourceType(sourceType)
                .setActionUrl(actionUrl);

        // Then
        assertThat(notification.getSourceId()).isEqualTo(sourceId);
        assertThat(notification.getSourceType()).isEqualTo(sourceType);
        assertThat(notification.getActionUrl()).isEqualTo(actionUrl);
    }
}
