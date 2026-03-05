package cloud.xcan.angus.core.repo.domain.cleanup;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 清理统计值对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CleanupStatistics {
    
    /**
     * 删除的制品数量
     */
    private Integer deletedArtifacts = 0;
    
    /**
     * 释放的存储空间（字节）
     */
    private Long freedSpaceBytes = 0L;
    
    /**
     * 格式化的释放空间大小
     */
    private String freedSpace;
    
    /**
     * 执行时间
     */
    private LocalDateTime executedAt;
    
    /**
     * 执行时长（秒）
     */
    private Long durationSeconds = 0L;
    
    /**
     * 扫描的制品数量
     */
    private Integer scannedArtifacts = 0;
    
    /**
     * 跳过的制品数量
     */
    private Integer skippedArtifacts = 0;
    
    /**
     * 删除的制品名称列表
     */
    private List<String> deletedArtifactNames;
    
    /**
     * 错误详情
     */
    private String errorDetails;

    /**
     * 计算并设置格式化的释放空间
     */
    public void calculateFreedSpace() {
        this.freedSpace = formatFileSize(freedSpaceBytes != null ? freedSpaceBytes : 0L);
    }

    /**
     * 格式化文件大小
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    /**
     * 获取格式化的执行时长
     */
    public String getFormattedDuration() {
        if (durationSeconds == null || durationSeconds == 0) {
            return "0秒";
        }
        
        long seconds = durationSeconds;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;
        
        if (hours > 0) {
            return String.format("%d小时%d分钟%d秒", hours, minutes, remainingSeconds);
        } else if (minutes > 0) {
            return String.format("%d分钟%d秒", minutes, remainingSeconds);
        } else {
            return String.format("%d秒", remainingSeconds);
        }
    }

    /**
     * 检查是否有错误
     */
    public boolean hasError() {
        return errorDetails != null && !errorDetails.trim().isEmpty();
    }

    /**
     * 添加删除的制品名称
     */
    public void addDeletedArtifact(String artifactName) {
        if (deletedArtifactNames == null) {
            deletedArtifactNames = new java.util.ArrayList<>();
        }
        deletedArtifactNames.add(artifactName);
        deletedArtifacts = deletedArtifactNames.size();
    }
}