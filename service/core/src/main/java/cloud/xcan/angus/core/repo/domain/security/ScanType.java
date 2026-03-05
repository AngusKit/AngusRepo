package cloud.xcan.angus.core.repo.domain.security;

public enum ScanType {
    VULNERABILITY("vulnerability", "漏洞扫描"),
    LICENSE("license", "许可证扫描"),
    MALWARE("malware", "恶意软件扫描"),
    FULL("full", "全面扫描");

    private final String value;
    private final String description;

    ScanType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() { return value; }
    public String getDescription() { return description; }

    public static ScanType fromValue(String value) {
        for (ScanType type : values()) {
            if (type.value.equals(value)) return type;
        }
        throw new IllegalArgumentException("Unknown scan type: " + value);
    }
}
