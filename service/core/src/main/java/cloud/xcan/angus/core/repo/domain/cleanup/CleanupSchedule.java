package cloud.xcan.angus.core.repo.domain.cleanup;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 调度配置值对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CleanupSchedule {
    
    /**
     * 调度类型
     */
    private ScheduleType type;
    
    /**
     * CRON表达式（当type为CRON时使用）
     */
    private String cronExpression;
    
    /**
     * 间隔小时数（当type为简单调度时使用）
     */
    private Integer intervalHours;
    
    /**
     * 执行时间（当type为DAILY/WEEKLY/MONTHLY时使用）
     */
    private LocalTime executeTime;

    /**
     * 验证调度配置是否有效
     */
    public boolean isValid() {
        if (type == null) {
            return false;
        }
        
        switch (type) {
            case CRON:
                return cronExpression != null && !cronExpression.trim().isEmpty();
            case ONCE:
                return true; // 一次性执行不需要额外配置
            case DAILY:
            case WEEKLY:
            case MONTHLY:
                return executeTime != null;
            default:
                return false;
        }
    }

    /**
     * 获取调度描述
     */
    public String getScheduleDescription() {
        if (type == null) {
            return "未配置";
        }
        
        switch (type) {
            case ONCE:
                return "执行一次";
            case DAILY:
                return "每日" + (executeTime != null ? executeTime.toString() : "") + "执行";
            case WEEKLY:
                return "每周" + (executeTime != null ? executeTime.toString() : "") + "执行";
            case MONTHLY:
                return "每月" + (executeTime != null ? executeTime.toString() : "") + "执行";
            case CRON:
                return "CRON: " + (cronExpression != null ? cronExpression : "");
            default:
                return type.getDescription();
        }
    }
}