package cloud.xcan.angus.core.repo.domain.cleanup;

/**
 * 调度类型枚举
 */
public enum ScheduleType {
    ONCE("once", "执行一次"),
    DAILY("daily", "每日执行"),
    WEEKLY("weekly", "每周执行"),
    MONTHLY("monthly", "每月执行"),
    CRON("cron", "CRON表达式");

    private final String value;
    private final String description;

    ScheduleType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据值获取枚举
     */
    public static ScheduleType fromValue(String value) {
        for (ScheduleType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown schedule type: " + value);
    }

    /**
     * 检查是否需要CRON表达式
     */
    public boolean requiresCronExpression() {
        return this == CRON;
    }

    /**
     * 检查是否为简单调度类型
     */
    public boolean isSimpleSchedule() {
        return this != CRON && this != ONCE;
    }
}