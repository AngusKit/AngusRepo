package cloud.xcan.angus.core.repo.domain.cleanup;

/**
 * 清理类型枚举
 */
public enum CleanupType {
    BY_AGE("by_age", "按时间清理"),
    BY_COUNT("by_count", "按数量清理"),
    BY_SIZE("by_size", "按大小清理"),
    BY_PATTERN("by_pattern", "按模式清理");

    private final String value;
    private final String description;

    CleanupType(String value, String description) {
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
    public static CleanupType fromValue(String value) {
        for (CleanupType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown cleanup type: " + value);
    }
}