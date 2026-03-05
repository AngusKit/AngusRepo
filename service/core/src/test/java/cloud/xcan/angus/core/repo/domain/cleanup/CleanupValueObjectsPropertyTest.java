package cloud.xcan.angus.core.repo.domain.cleanup;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * 清理策略值对象属性测试
 * 
 * 验证值对象的不变性和业务逻辑
 */
public class CleanupValueObjectsPropertyTest {

    /**
     * 属性测试：清理统计对象的一致性
     * 验证统计数据的计算和格式化
     */
    @Property(tries = 100)
    void cleanupStatisticsConsistency(
            @ForAll @IntRange(min = 0, max = 10000) Integer deletedArtifacts,
            @ForAll @LongRange(min = 0, max = 1000000000L) Long freedSpaceBytes,
            @ForAll @LongRange(min = 0, max = 86400L) Long durationSeconds,
            @ForAll @IntRange(min = 0, max = 10000) Integer scannedArtifacts,
            @ForAll @IntRange(min = 0, max = 10000) Integer skippedArtifacts) {

        // Given: 创建清理统计对象
        CleanupStatistics stats = new CleanupStatistics();
        stats.setDeletedArtifacts(deletedArtifacts);
        stats.setFreedSpaceBytes(freedSpaceBytes);
        stats.setDurationSeconds(durationSeconds);
        stats.setScannedArtifacts(scannedArtifacts);
        stats.setSkippedArtifacts(skippedArtifacts);
        stats.setExecutedAt(LocalDateTime.now());

        // When: 计算格式化的释放空间
        stats.calculateFreedSpace();

        // Then: 验证统计数据的一致性
        assertThat(stats.getDeletedArtifacts()).isEqualTo(deletedArtifacts);
        assertThat(stats.getFreedSpaceBytes()).isEqualTo(freedSpaceBytes);
        assertThat(stats.getDurationSeconds()).isEqualTo(durationSeconds);
        assertThat(stats.getScannedArtifacts()).isEqualTo(scannedArtifacts);
        assertThat(stats.getSkippedArtifacts()).isEqualTo(skippedArtifacts);
        assertThat(stats.getFreedSpace()).isNotNull();
        assertThat(stats.getExecutedAt()).isNotNull();
    }

    /**
     * 属性测试：文件大小格式化的一致性
     * 验证不同大小的文件格式化结果
     */
    @Property(tries = 100)
    void fileSizeFormattingConsistency(@ForAll @LongRange(min = 0, max = Long.MAX_VALUE / 2) Long bytes) {
        // When: 格式化文件大小
        String formatted = CleanupStatistics.formatFileSize(bytes);

        // Then: 验证格式化结果的一致性
        assertThat(formatted).isNotNull();
        assertThat(formatted).isNotEmpty();
        
        if (bytes < 1024) {
            assertThat(formatted).endsWith(" B");
        } else {
            assertThat(formatted).matches("\\d+\\.\\d+ [KMGTPE]B");
        }
    }

    /**
     * 属性测试：执行时长格式化的一致性
     * 验证不同时长的格式化结果
     */
    @Property(tries = 100)
    void durationFormattingConsistency(@ForAll @LongRange(min = 0, max = 86400L) Long seconds) {
        // Given: 创建统计对象并设置时长
        CleanupStatistics stats = new CleanupStatistics();
        stats.setDurationSeconds(seconds);

        // When: 获取格式化的时长
        String formatted = stats.getFormattedDuration();

        // Then: 验证格式化结果的一致性
        assertThat(formatted).isNotNull();
        assertThat(formatted).isNotEmpty();
        
        if (seconds == 0) {
            assertThat(formatted).isEqualTo("0秒");
        } else if (seconds < 60) {
            assertThat(formatted).endsWith("秒");
        } else if (seconds < 3600) {
            assertThat(formatted).contains("分钟");
        } else {
            assertThat(formatted).contains("小时");
        }
    }

    /**
     * 属性测试：清理条件验证的完整性
     * 验证所有清理类型的条件验证逻辑
     */
    @Property(tries = 100)
    void cleanupConditionValidationCompleteness(
            @ForAll @Positive Integer days,
            @ForAll @Positive Integer versions,
            @ForAll @Positive Long bytes,
            @ForAll @AlphaChars @StringLength(min = 1, max = 100) String pattern) {

        // 测试按时间清理的条件
        CleanupCondition ageCondition = new CleanupCondition();
        ageCondition.setOlderThanDays(days);
        assertThat(ageCondition.isValid(CleanupType.BY_AGE)).isTrue();
        assertThat(ageCondition.isValid(CleanupType.BY_COUNT)).isFalse();
        assertThat(ageCondition.isValid(CleanupType.BY_SIZE)).isFalse();
        assertThat(ageCondition.isValid(CleanupType.BY_PATTERN)).isFalse();

        // 测试按数量清理的条件
        CleanupCondition countCondition = new CleanupCondition();
        countCondition.setKeepLastVersions(versions);
        assertThat(countCondition.isValid(CleanupType.BY_COUNT)).isTrue();
        assertThat(countCondition.isValid(CleanupType.BY_AGE)).isFalse();
        assertThat(countCondition.isValid(CleanupType.BY_SIZE)).isFalse();
        assertThat(countCondition.isValid(CleanupType.BY_PATTERN)).isFalse();

        // 测试按大小清理的条件
        CleanupCondition sizeCondition = new CleanupCondition();
        sizeCondition.setMaxSizeBytes(bytes);
        assertThat(sizeCondition.isValid(CleanupType.BY_SIZE)).isTrue();
        assertThat(sizeCondition.isValid(CleanupType.BY_AGE)).isFalse();
        assertThat(sizeCondition.isValid(CleanupType.BY_COUNT)).isFalse();
        assertThat(sizeCondition.isValid(CleanupType.BY_PATTERN)).isFalse();

        // 测试按模式清理的条件
        CleanupCondition patternCondition = new CleanupCondition();
        patternCondition.setNamePattern(pattern);
        assertThat(patternCondition.isValid(CleanupType.BY_PATTERN)).isTrue();
        assertThat(patternCondition.isValid(CleanupType.BY_AGE)).isFalse();
        assertThat(patternCondition.isValid(CleanupType.BY_COUNT)).isFalse();
        assertThat(patternCondition.isValid(CleanupType.BY_SIZE)).isFalse();
    }

    /**
     * 属性测试：调度配置验证的完整性
     * 验证所有调度类型的配置验证逻辑
     */
    @Property(tries = 100)
    void cleanupScheduleValidationCompleteness(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String cronExpr,
            @ForAll @IntRange(min = 0, max = 23) Integer hour,
            @ForAll @IntRange(min = 0, max = 59) Integer minute) {

        LocalTime executeTime = LocalTime.of(hour, minute);

        // 测试ONCE类型
        CleanupSchedule onceSchedule = new CleanupSchedule();
        onceSchedule.setType(ScheduleType.ONCE);
        assertThat(onceSchedule.isValid()).isTrue();

        // 测试CRON类型
        CleanupSchedule cronSchedule = new CleanupSchedule();
        cronSchedule.setType(ScheduleType.CRON);
        cronSchedule.setCronExpression(cronExpr);
        assertThat(cronSchedule.isValid()).isTrue();

        // 测试时间调度类型
        for (ScheduleType type : Arrays.asList(ScheduleType.DAILY, ScheduleType.WEEKLY, ScheduleType.MONTHLY)) {
            CleanupSchedule timeSchedule = new CleanupSchedule();
            timeSchedule.setType(type);
            timeSchedule.setExecuteTime(executeTime);
            assertThat(timeSchedule.isValid()).isTrue();
        }
    }

    /**
     * 属性测试：调度描述的一致性
     * 验证调度描述的生成逻辑
     */
    @Property(tries = 100)
    void scheduleDescriptionConsistency(
            @ForAll ScheduleType type,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String cronExpr,
            @ForAll @IntRange(min = 0, max = 23) Integer hour) {

        // Given: 创建调度配置
        CleanupSchedule schedule = new CleanupSchedule();
        schedule.setType(type);
        
        if (type == ScheduleType.CRON) {
            schedule.setCronExpression(cronExpr);
        } else if (type != ScheduleType.ONCE) {
            schedule.setExecuteTime(LocalTime.of(hour, 0));
        }

        // When: 获取调度描述
        String description = schedule.getScheduleDescription();

        // Then: 验证描述的一致性
        assertThat(description).isNotNull();
        assertThat(description).isNotEmpty();
        
        switch (type) {
            case ONCE:
                assertThat(description).isEqualTo("执行一次");
                break;
            case DAILY:
                assertThat(description).contains("每日");
                break;
            case WEEKLY:
                assertThat(description).contains("每周");
                break;
            case MONTHLY:
                assertThat(description).contains("每月");
                break;
            case CRON:
                assertThat(description).startsWith("CRON: ");
                break;
        }
    }

    /**
     * 属性测试：统计对象的错误处理
     * 验证错误信息的处理逻辑
     */
    @Property(tries = 100)
    void statisticsErrorHandling(@ForAll @StringLength(max = 1000) String errorDetails) {
        // Given: 创建统计对象并设置错误信息
        CleanupStatistics stats = new CleanupStatistics();
        stats.setErrorDetails(errorDetails);

        // Then: 验证错误处理的一致性
        boolean expectedHasError = errorDetails != null && !errorDetails.trim().isEmpty();
        assertThat(stats.hasError()).isEqualTo(expectedHasError);
    }

    /**
     * 属性测试：删除制品名称列表的管理
     * 验证删除制品名称的添加和统计
     */
    @Property(tries = 100)
    void deletedArtifactsManagement(
            @ForAll @AlphaChars @StringLength(min = 1, max = 100) String artifactName,
            @ForAll @IntRange(min = 1, max = 10) Integer count) {

        // Given: 创建统计对象
        CleanupStatistics stats = new CleanupStatistics();

        // When: 添加多个删除的制品名称
        for (int i = 0; i < count; i++) {
            stats.addDeletedArtifact(artifactName + "_" + i);
        }

        // Then: 验证删除制品的管理
        assertThat(stats.getDeletedArtifactNames()).hasSize(count);
        assertThat(stats.getDeletedArtifacts()).isEqualTo(count);
        
        for (int i = 0; i < count; i++) {
            assertThat(stats.getDeletedArtifactNames()).contains(artifactName + "_" + i);
        }
    }

    // 生成器方法
    @Provide
    Arbitrary<ScheduleType> scheduleType() {
        return Arbitraries.of(ScheduleType.values());
    }

    /**
     * 单元测试：验证边界值的处理
     */
    @Test
    void testBoundaryValues() {
        // 测试零值的文件大小格式化
        assertThat(CleanupStatistics.formatFileSize(0L)).isEqualTo("0 B");
        
        // 测试1KB边界
        assertThat(CleanupStatistics.formatFileSize(1023L)).isEqualTo("1023 B");
        assertThat(CleanupStatistics.formatFileSize(1024L)).isEqualTo("1.0 KB");
        
        // 测试零时长的格式化
        CleanupStatistics stats = new CleanupStatistics();
        stats.setDurationSeconds(0L);
        assertThat(stats.getFormattedDuration()).isEqualTo("0秒");
        
        // 测试null时长的处理
        stats.setDurationSeconds(null);
        assertThat(stats.getFormattedDuration()).isEqualTo("0秒");
    }

    /**
     * 单元测试：验证调度类型的业务方法
     */
    @Test
    void testScheduleTypeBusinessMethods() {
        // 验证需要CRON表达式的类型
        assertThat(ScheduleType.CRON.requiresCronExpression()).isTrue();
        assertThat(ScheduleType.ONCE.requiresCronExpression()).isFalse();
        assertThat(ScheduleType.DAILY.requiresCronExpression()).isFalse();
        
        // 验证简单调度类型
        assertThat(ScheduleType.DAILY.isSimpleSchedule()).isTrue();
        assertThat(ScheduleType.WEEKLY.isSimpleSchedule()).isTrue();
        assertThat(ScheduleType.MONTHLY.isSimpleSchedule()).isTrue();
        assertThat(ScheduleType.CRON.isSimpleSchedule()).isFalse();
        assertThat(ScheduleType.ONCE.isSimpleSchedule()).isFalse();
    }
}