package cloud.xcan.angus.core.repo.domain.notification;

public enum NotificationType {
    SECURITY("security", "安全通知"),
    STORAGE("storage", "存储通知"),
    ACCESS("access", "访问通知"),
    ARTIFACT("artifact", "制品通知"),
    SYSTEM("system", "系统通知");

    private final String value;
    private final String description;

    NotificationType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() { return value; }
    public String getDescription() { return description; }

    public static NotificationType fromValue(String value) {
        for (NotificationType type : values()) {
            if (type.value.equals(value)) return type;
        }
        throw new IllegalArgumentException("Unknown notification type: " + value);
    }
}
