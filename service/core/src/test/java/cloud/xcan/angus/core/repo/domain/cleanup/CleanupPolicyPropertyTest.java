package cloud.xcan.angus.core.repo.domain.cleanup;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.DateTimes;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * 清理策略领域层属性测试
 * 
 * 属性 1: 清理策略CRUD操作一致性
 * 验证需求: 需求 1.1, 1.3, 1.4, 1.5
 */
public class CleanupPolicyPropertyTest {

    /**
     * 属性测试：清理策略实体的基本属性一致性
     * 验证策略创建后，所有字段都能正确设置和获取
     */
    @Property(tries = 100)
    void cleanupPolicyBasicPropertiesConsistency(
            @ForAll @AlphaChars @StringLength(min = 1, max = 64) String id,
            @ForAll @AlphaChars @StringLength(min = 1, max = 255) String name,
            @ForAll @StringLength(max = 1000) String description,
            @ForAll @AlphaChars @StringLength(min = 1, max = 64) String repositoryId,
            @ForAll CleanupType type,
            @ForAll boolean enabled,
            @ForAll boolean dryRun) {

        // Given: 创建清理策略实体
        CleanupPolicy policy = new CleanupPolicy()
                .setId(id)
                .setName(name)
                .setDescription(description)
                .setRepositoryId(repositoryId)
                .setType(type)
                .setEnabled(enabled)
                .setDryRun(dryRun);

        // Then: 验证所有属性都能正确设置和获取
        assertThat(policy.getId()).isEqualTo(id);
        assertThat(policy.getName()).isEqualTo(name);
        assertThat(policy.getDescription()).isEqualTo(description);
        assertThat(policy.getRepositoryId()).isEqualTo(repositoryId);
        assertThat(policy.getType()).isEqualTo(type);
        assertThat(policy.getEnabled()).isEqualTo(enabled);
        assertThat(policy.getDryRun()).isEqualTo(dryRun);
        assertThat(policy.identity()).isEqualTo(id);
    }

    /**
     * 属性测试：清理条件验证的一致性
     * 验证不同清理类型对应的条件验证逻辑
     */
    @Property(tries = 100)
    void cleanupConditionValidationConsistency(
            @ForAll CleanupType type,
            @ForAll @Positive Integer days,
            @ForAll @Positive Integer versions,
            @ForAll @Positive Long bytes,
            @ForAll @AlphaChars @StringLength(min = 1, max = 100) String pattern) {

        // Given: 根据清理类型创建对应的清理条件
        CleanupCondition condition = new CleanupCondition();
        
        switch (type) {
            case BY_AGE:
                condition.setOlderThanDays(days);
                break;
            case BY_COUNT:
                condition.setKeepLastVersions(versions);
                break;
            case BY_SIZE:
                condition.setMaxSizeBytes(bytes);
                break;
            case BY_PATTERN:
                condition.setNamePattern(pattern);
                break;
        }

        // Then: 验证条件验证逻辑的一致性
        assertThat(condition.isValid(type)).isTrue();
        
        // 验证其他类型的条件应该无效
        for (CleanupType otherType : CleanupType.values()) {
            if (otherType != type) {
                assertThat(condition.isValid(otherType)).isFalse();
            }
        }
    }

    /**
     * 属性测试：调度配置验证的一致性
     * 验证不同调度类型对应的配置验证逻辑
     */
    @Property(tries = 100)
    void cleanupScheduleValidationConsistency(
            @ForAll ScheduleType type,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String cronExpr,
            @ForAll @IntRange(min = 1, max = 24) Integer hours) {

        // Given: 根据调度类型创建对应的调度配置
        CleanupSchedule schedule = new CleanupSchedule();
        schedule.setType(type);
        
        switch (type) {
            case CRON:
                schedule.setCronExpression(cronExpr);
                break;
            case DAILY:
            case WEEKLY:
            case MONTHLY:
                schedule.setExecuteTime(LocalTime.of(hours % 24, 0));
                break;
            case ONCE:
                // 一次性执行不需要额外配置
                break;
        }

        // Then: 验证调度配置验证逻辑的一致性
        boolean expectedValid = type == ScheduleType.ONCE || 
                               (type == ScheduleType.CRON && cronExpr != null && !cronExpr.trim().isEmpty()) ||
                               (type != ScheduleType.CRON && type != ScheduleType.ONCE);
        
        assertThat(schedule.isValid()).isEqualTo(expectedValid);
    }

    /**
     * 属性测试：审计字段的一致性
     * 验证审计字段的设置和获取
     */
    @Property(tries = 100)
    void auditFieldsConsistency(
            @ForAll @Positive Long createdBy,
            @ForAll @Positive Long modifiedBy) {

        // Given: 创建策略并设置审计字段
        CleanupPolicy policy = new CleanupPolicy();
        LocalDateTime now = LocalDateTime.now();
        
        policy.setCreatedBy(createdBy);
        policy.setCreatedDate(now);
        policy.setModifiedBy(modifiedBy);
        policy.setModifiedDate(now);

        // Then: 验证审计字段的一致性
        assertThat(policy.getCreatedBy()).isEqualTo(createdBy);
        assertThat(policy.getCreatedDate()).isEqualTo(now);
        assertThat(policy.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(policy.getModifiedDate()).isEqualTo(now);
    }

    /**
     * 属性测试：执行统计的一致性
     * 验证执行次数和统计信息的更新逻辑
     */
    @Property(tries = 100)
    void executionStatisticsConsistency(
            @ForAll @IntRange(min = 0, max = 1000) Integer initialCount,
            @ForAll @IntRange(min = 1, max = 10) Integer increments) {

        // Given: 创建策略并设置初始执行次数
        CleanupPolicy policy = new CleanupPolicy();
        policy.setExecutionCount(initialCount);

        // When: 模拟多次执行
        int expectedCount = initialCount;
        for (int i = 0; i < increments; i++) {
            expectedCount++;
            policy.setExecutionCount(expectedCount);
            policy.setLastExecuted(LocalDateTime.now());
        }

        // Then: 验证执行统计的一致性
        assertThat(policy.getExecutionCount()).isEqualTo(expectedCount);
        assertThat(policy.getLastExecuted()).isNotNull();
    }

    // 生成器方法
    @Provide
    Arbitrary<CleanupType> cleanupType() {
        return Arbitraries.of(CleanupType.values());
    }

    @Provide
    Arbitrary<ScheduleType> scheduleType() {
        return Arbitraries.of(ScheduleType.values());
    }

    @Provide
    Arbitrary<CleanupStatus> cleanupStatus() {
        return Arbitraries.of(CleanupStatus.values());
    }

    /**
     * 单元测试：验证枚举值的正确性
     */
    @Test
    void testCleanupTypeEnumValues() {
        // 验证所有清理类型都有正确的值和描述
        assertThat(CleanupType.BY_AGE.getValue()).isEqualTo("by_age");
        assertThat(CleanupType.BY_AGE.getDescription()).isEqualTo("按时间清理");
        
        assertThat(CleanupType.BY_COUNT.getValue()).isEqualTo("by_count");
        assertThat(CleanupType.BY_COUNT.getDescription()).isEqualTo("按数量清理");
        
        assertThat(CleanupType.BY_SIZE.getValue()).isEqualTo("by_size");
        assertThat(CleanupType.BY_SIZE.getDescription()).isEqualTo("按大小清理");
        
        assertThat(CleanupType.BY_PATTERN.getValue()).isEqualTo("by_pattern");
        assertThat(CleanupType.BY_PATTERN.getDescription()).isEqualTo("按模式清理");
    }

    @Test
    void testCleanupStatusEnumValues() {
        // 验证所有清理状态都有正确的值和描述
        assertThat(CleanupStatus.PENDING.getValue()).isEqualTo("pending");
        assertThat(CleanupStatus.PENDING.getDescription()).isEqualTo("等待执行");
        
        assertThat(CleanupStatus.RUNNING.getValue()).isEqualTo("running");
        assertThat(CleanupStatus.RUNNING.getDescription()).isEqualTo("执行中");
        
        assertThat(CleanupStatus.COMPLETED.getValue()).isEqualTo("completed");
        assertThat(CleanupStatus.COMPLETED.getDescription()).isEqualTo("执行完成");
        
        assertThat(CleanupStatus.FAILED.getValue()).isEqualTo("failed");
        assertThat(CleanupStatus.FAILED.getDescription()).isEqualTo("执行失败");
        
        assertThat(CleanupStatus.CANCELLED.getValue()).isEqualTo("cancelled");
        assertThat(CleanupStatus.CANCELLED.getDescription()).isEqualTo("已取消");
    }

    @Test
    void testScheduleTypeEnumValues() {
        // 验证所有调度类型都有正确的值和描述
        assertThat(ScheduleType.ONCE.getValue()).isEqualTo("once");
        assertThat(ScheduleType.ONCE.getDescription()).isEqualTo("执行一次");
        
        assertThat(ScheduleType.DAILY.getValue()).isEqualTo("daily");
        assertThat(ScheduleType.DAILY.getDescription()).isEqualTo("每日执行");
        
        assertThat(ScheduleType.WEEKLY.getValue()).isEqualTo("weekly");
        assertThat(ScheduleType.WEEKLY.getDescription()).isEqualTo("每周执行");
        
        assertThat(ScheduleType.MONTHLY.getValue()).isEqualTo("monthly");
        assertThat(ScheduleType.MONTHLY.getDescription()).isEqualTo("每月执行");
        
        assertThat(ScheduleType.CRON.getValue()).isEqualTo("cron");
        assertThat(ScheduleType.CRON.getDescription()).isEqualTo("CRON表达式");
    }
}