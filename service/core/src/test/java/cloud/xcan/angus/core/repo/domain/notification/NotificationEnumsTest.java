package cloud.xcan.angus.core.repo.domain.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class NotificationEnumsTest {

    @Test
    void testNotificationType_Values() {
        assertThat(NotificationType.values()).hasSize(5);
        assertThat(NotificationType.SECURITY.getValue()).isEqualTo("security");
        assertThat(NotificationType.STORAGE.getValue()).isEqualTo("storage");
        assertThat(NotificationType.ACCESS.getValue()).isEqualTo("access");
        assertThat(NotificationType.ARTIFACT.getValue()).isEqualTo("artifact");
        assertThat(NotificationType.SYSTEM.getValue()).isEqualTo("system");
    }

    @Test
    void testNotificationType_FromValue() {
        assertThat(NotificationType.fromValue("security")).isEqualTo(NotificationType.SECURITY);
        assertThat(NotificationType.fromValue("system")).isEqualTo(NotificationType.SYSTEM);
        assertThatThrownBy(() -> NotificationType.fromValue("invalid"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testNotificationType_Descriptions() {
        assertThat(NotificationType.SECURITY.getDescription()).isEqualTo("安全通知");
        assertThat(NotificationType.STORAGE.getDescription()).isEqualTo("存储通知");
        assertThat(NotificationType.ACCESS.getDescription()).isEqualTo("访问通知");
        assertThat(NotificationType.ARTIFACT.getDescription()).isEqualTo("制品通知");
        assertThat(NotificationType.SYSTEM.getDescription()).isEqualTo("系统通知");
    }

    @Test
    void testNotificationPriority_Values() {
        assertThat(NotificationPriority.values()).hasSize(3);
        assertThat(NotificationPriority.HIGH.getValue()).isEqualTo("high");
        assertThat(NotificationPriority.MEDIUM.getValue()).isEqualTo("medium");
        assertThat(NotificationPriority.LOW.getValue()).isEqualTo("low");
    }

    @Test
    void testNotificationPriority_FromValue() {
        assertThat(NotificationPriority.fromValue("high")).isEqualTo(NotificationPriority.HIGH);
        assertThat(NotificationPriority.fromValue("medium")).isEqualTo(NotificationPriority.MEDIUM);
        assertThat(NotificationPriority.fromValue("low")).isEqualTo(NotificationPriority.LOW);
        assertThatThrownBy(() -> NotificationPriority.fromValue("invalid"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testNotificationPriority_Descriptions() {
        assertThat(NotificationPriority.HIGH.getDescription()).isEqualTo("高");
        assertThat(NotificationPriority.MEDIUM.getDescription()).isEqualTo("中");
        assertThat(NotificationPriority.LOW.getDescription()).isEqualTo("低");
    }
}
