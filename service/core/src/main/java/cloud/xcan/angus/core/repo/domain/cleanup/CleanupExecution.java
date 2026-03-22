package cloud.xcan.angus.core.repo.domain.cleanup;

import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 清理执行记录实体
 */
@Entity
@Table(name = "cleanup_execution")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class CleanupExecution extends TenantEntity<CleanupExecution, String> {

    // 常量定义
    public static final int MAX_ID_LENGTH = 64;
    public static final int MAX_DESC_LENGTH_X4 = 4000;

    @Id
    @Column(length = MAX_ID_LENGTH)
    private String id;

    @Column(name = "policy_id", nullable = false, length = MAX_ID_LENGTH)
    private String policyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CleanupStatus status = CleanupStatus.PENDING;

    @Column
    private Integer progress = 0;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "error_message", length = MAX_DESC_LENGTH_X4)
    private String errorMessage;

    // JSON字段存储
    @Column(name = "statistics", columnDefinition = "JSON")
    private String statisticsJson;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    // 临时字段（不持久化）- 用于存储反序列化的JSON对象
    @Transient
    private CleanupStatistics statistics;

    // 临时字段 - 用于临时存储关联数据
    @Transient
    private String policyName;

    @Transient
    private Long durationSeconds;

    @Override
    public String identity() {
        return this.id;
    }

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }

    /**
     * 计算执行时长（秒）
     */
    public Long calculateDurationSeconds() {
        if (startTime != null && endTime != null) {
            return java.time.Duration.between(startTime, endTime).getSeconds();
        }
        return null;
    }

    /**
     * 检查是否正在执行中
     */
    public boolean isRunning() {
        return status == CleanupStatus.RUNNING || status == CleanupStatus.PENDING;
    }

    /**
     * 检查是否已完成（成功或失败）
     */
    public boolean isFinished() {
        return status == CleanupStatus.COMPLETED || 
               status == CleanupStatus.FAILED || 
               status == CleanupStatus.CANCELLED;
    }
}