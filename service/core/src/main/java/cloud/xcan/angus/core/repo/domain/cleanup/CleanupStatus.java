package cloud.xcan.angus.core.repo.domain.cleanup;

/**
 * 清理状态枚举
 */
public enum CleanupStatus {
    PENDING("pending", "等待执行"),
    RUNNING("running", "执行中"),
    COMPLETED("completed", "执行完成"),
    FAILED("failed", "执行失败"),
    CANCELLED("cancelled", "已取消");

    private final String value;
    private final String description;

    CleanupStatus(String value, String description) {
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
    public static CleanupStatus fromValue(String value) {
        for (CleanupStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown cleanup status: " + value);
    }

    /**
     * 检查是否为运行中状态
     */
    public boolean isRunning() {
        return this == RUNNING || this == PENDING;
    }

    /**
     * 检查是否为完成状态
     */
    public boolean isFinished() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }
}