package cloud.xcan.angus.core.repo.domain.cleanup;

import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 清理策略实体
 */
@Entity
@Table(name = "cleanup_policy")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class CleanupPolicy extends TenantAuditingEntity<CleanupPolicy, String> {

    // 常量定义
    public static final int MAX_ID_LENGTH = 64;
    public static final int MAX_NAME_LENGTH = 255;
    public static final int MAX_DESC_LENGTH = 1000;

    @Id
    @Column(length = MAX_ID_LENGTH)
    private String id;

    @Column(nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    @Column(length = MAX_DESC_LENGTH)
    private String description;

    @Column(name = "repository_id", nullable = false, length = MAX_ID_LENGTH)
    private String repositoryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CleanupType type;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name = "dry_run")
    private Boolean dryRun = false;

    @Column(name = "last_executed")
    private LocalDateTime lastExecuted;

    @Column(name = "next_execution")
    private LocalDateTime nextExecution;

    @Column(name = "execution_count")
    private Integer executionCount = 0;

    // JSON字段存储
    @Column(name = "condition", columnDefinition = "JSON")
    private String conditionJson;

    @Column(name = "schedule", columnDefinition = "JSON")
    private String scheduleJson;

    @Column(name = "last_execution_stats", columnDefinition = "JSON")
    private String lastExecutionStatsJson;

    // 临时字段（不持久化）- 用于存储反序列化的JSON对象
    @Transient
    private CleanupCondition condition;

    @Transient
    private CleanupSchedule schedule;

    @Transient
    private CleanupStatistics lastExecutionStats;

    // 临时字段 - 用于临时存储关联数据
    @Transient
    private String repositoryName;

    @Transient
    private UserInfo creator;

    @Transient
    private UserInfo modifier;

    @Override
    public String identity() {
        return this.id;
    }

    /**
     * 用户信息内部类
     */
    public static class UserInfo {
        private Long id;
        private String name;
        private String email;

        public UserInfo() {}

        public UserInfo(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}