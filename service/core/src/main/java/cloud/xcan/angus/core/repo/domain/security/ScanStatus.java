package cloud.xcan.angus.core.repo.domain.security;

public enum ScanStatus {
    PENDING("pending", "等待扫描"),
    SCANNING("scanning", "扫描中"),
    COMPLETED("completed", "扫描完成"),
    FAILED("failed", "扫描失败"),
    CANCELLED("cancelled", "已取消");

    private final String value;
    private final String description;

    ScanStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() { return value; }
    public String getDescription() { return description; }

    public static ScanStatus fromValue(String value) {
        for (ScanStatus status : values()) {
            if (status.value.equals(value)) return status;
        }
        throw new IllegalArgumentException("Unknown scan status: " + value);
    }

    public boolean isRunning() { return this == SCANNING || this == PENDING; }
    public boolean isFinished() { return this == COMPLETED || this == FAILED || this == CANCELLED; }
}
