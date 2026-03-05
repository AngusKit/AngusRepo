package cloud.xcan.angus.core.repo.domain.cleanup;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 清理条件值对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CleanupCondition {
    
    /**
     * 超过N天未使用（按时间清理）
     */
    private Integer olderThanDays;
    
    /**
     * 保留最新N个版本（按数量清理）
     */
    private Integer keepLastVersions;
    
    /**
     * 最大存储字节数（按大小清理）
     */
    private Long maxSizeBytes;
    
    /**
     * 名称匹配模式（正则表达式）（按模式清理）
     */
    private String namePattern;
    
    /**
     * 最小下载次数（可选条件）
     */
    private Integer minDownloads;
    
    /**
     * 排除模式列表（可选条件）
     */
    private List<String> excludePatterns;

    /**
     * 验证清理条件是否有效
     */
    public boolean isValid(CleanupType type) {
        switch (type) {
            case BY_AGE:
                return olderThanDays != null && olderThanDays > 0;
            case BY_COUNT:
                return keepLastVersions != null && keepLastVersions > 0;
            case BY_SIZE:
                return maxSizeBytes != null && maxSizeBytes > 0;
            case BY_PATTERN:
                return namePattern != null && !namePattern.trim().isEmpty();
            default:
                return false;
        }
    }

    /**
     * 获取格式化的大小字符串
     */
    public String getFormattedMaxSize() {
        if (maxSizeBytes == null) {
            return null;
        }
        return formatFileSize(maxSizeBytes);
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}