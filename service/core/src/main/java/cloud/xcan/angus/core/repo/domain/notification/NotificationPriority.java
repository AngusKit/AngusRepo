package cloud.xcan.angus.core.repo.domain.notification;

public enum NotificationPriority {
    HIGH("high", "高"),
    MEDIUM("medium", "中"),
    LOW("low", "低");

    private final String value;
    private final String description;

    NotificationPriority(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() { return value; }
    public String getDescription() { return description; }

    public static NotificationPriority fromValue(String value) {
        for (NotificationPriority priority : values()) {
            if (priority.value.equals(value)) return priority;
        }
        throw new IllegalArgumentException("Unknown notification priority: " + value);
    }
}
