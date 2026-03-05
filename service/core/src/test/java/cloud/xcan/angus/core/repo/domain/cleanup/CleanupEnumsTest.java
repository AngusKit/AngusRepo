package cloud.xcan.angus.core.repo.domain.cleanup;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * 清理策略枚举类型单元测试
 * 
 * 测试枚举和值对象的正确性
 * 需求: 需求 1.1, 3.1
 */
public class CleanupEnumsTest {

    @Test
    void testCleanupTypeValues() {
        // Test all enum values exist
        CleanupType[] types = CleanupType.values();
        assertThat(types).hasSize(4);
        assertThat(types).contains(
            CleanupType.BY_AGE,
            CleanupType.BY_COUNT,
            CleanupType.BY_SIZE,
            CleanupType.BY_PATTERN
        );
    }

    @Test
    void testCleanupTypeValueAndDescription() {
        // Test BY_AGE
        assertThat(CleanupType.BY_AGE.getValue()).isEqualTo("by_age");
        assertThat(CleanupType.BY_AGE.getDescription()).isEqualTo("按时间清理");

        // Test BY_COUNT
        assertThat(CleanupType.BY_COUNT.getValue()).isEqualTo("by_count");
        assertThat(CleanupType.BY_COUNT.getDescription()).isEqualTo("按数量清理");

        // Test BY_SIZE
        assertThat(CleanupType.BY_SIZE.getValue()).isEqualTo("by_size");
        assertThat(CleanupType.BY_SIZE.getDescription()).isEqualTo("按大小清理");

        // Test BY_PATTERN
        assertThat(CleanupType.BY_PATTERN.getValue()).isEqualTo("by_pattern");
        assertThat(CleanupType.BY_PATTERN.getDescription()).isEqualTo("按模式清理");
    }

    @Test
    void testCleanupTypeFromValue() {
        // Test valid values
        assertThat(CleanupType.fromValue("by_age")).isEqualTo(CleanupType.BY_AGE);
        assertThat(CleanupType.fromValue("by_count")).isEqualTo(CleanupType.BY_COUNT);
        assertThat(CleanupType.fromValue("by_size")).isEqualTo(CleanupType.BY_SIZE);
        assertThat(CleanupType.fromValue("by_pattern")).isEqualTo(CleanupType.BY_PATTERN);
    }

    @Test
    void testCleanupTypeFromValueInvalid() {
        // Test invalid value
        assertThatThrownBy(() -> CleanupType.fromValue("invalid"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unknown cleanup type: invalid");
    }

    @Test
    void testCleanupTypeFromValueNull() {
        // Test null value
        assertThatThrownBy(() -> CleanupType.fromValue(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unknown cleanup type: null");
    }

    @Test
    void testCleanupStatusValues() {
        // Test all enum values exist
        CleanupStatus[] statuses = CleanupStatus.values();
        assertThat(statuses).hasSize(5);
        assertThat(statuses).contains(
            CleanupStatus.PENDING,
            CleanupStatus.RUNNING,
            CleanupStatus.COMPLETED,
            CleanupStatus.FAILED,
            CleanupStatus.CANCELLED
        );
    }

    @Test
    void testCleanupStatusValueAndDescription() {
        // Test PENDING
        assertThat(CleanupStatus.PENDING.getValue()).isEqualTo("pending");
        assertThat(CleanupStatus.PENDING.getDescription()).isEqualTo("等待执行");

        // Test RUNNING
        assertThat(CleanupStatus.RUNNING.getValue()).isEqualTo("running");
        assertThat(CleanupStatus.RUNNING.getDescription()).isEqualTo("执行中");

        // Test COMPLETED
        assertThat(CleanupStatus.COMPLETED.getValue()).isEqualTo("completed");
        assertThat(CleanupStatus.COMPLETED.getDescription()).isEqualTo("执行完成");

        // Test FAILED
        assertThat(CleanupStatus.FAILED.getValue()).isEqualTo("failed");
        assertThat(CleanupStatus.FAILED.getDescription()).isEqualTo("执行失败");

        // Test CANCELLED
        assertThat(CleanupStatus.CANCELLED.getValue()).isEqualTo("cancelled");
        assertThat(CleanupStatus.CANCELLED.getDescription()).isEqualTo("已取消");
    }

    @Test
    void testCleanupStatusFromValue() {
        // Test valid values
        assertThat(CleanupStatus.fromValue("pending")).isEqualTo(CleanupStatus.PENDING);
        assertThat(CleanupStatus.fromValue("running")).isEqualTo(CleanupStatus.RUNNING);
        assertThat(CleanupStatus.fromValue("completed")).isEqualTo(CleanupStatus.COMPLETED);
        assertThat(CleanupStatus.fromValue("failed")).isEqualTo(CleanupStatus.FAILED);
        assertThat(CleanupStatus.fromValue("cancelled")).isEqualTo(CleanupStatus.CANCELLED);
    }

    @Test
    void testCleanupStatusFromValueInvalid() {
        // Test invalid value
        assertThatThrownBy(() -> CleanupStatus.fromValue("invalid"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unknown cleanup status: invalid");
    }

    @Test
    void testCleanupStatusIsRunning() {
        // Test running statuses
        assertThat(CleanupStatus.PENDING.isRunning()).isTrue();
        assertThat(CleanupStatus.RUNNING.isRunning()).isTrue();

        // Test non-running statuses
        assertThat(CleanupStatus.COMPLETED.isRunning()).isFalse();
        assertThat(CleanupStatus.FAILED.isRunning()).isFalse();
        assertThat(CleanupStatus.CANCELLED.isRunning()).isFalse();
    }

    @Test
    void testCleanupStatusIsFinished() {
        // Test non-finished statuses
        assertThat(CleanupStatus.PENDING.isFinished()).isFalse();
        assertThat(CleanupStatus.RUNNING.isFinished()).isFalse();

        // Test finished statuses
        assertThat(CleanupStatus.COMPLETED.isFinished()).isTrue();
        assertThat(CleanupStatus.FAILED.isFinished()).isTrue();
        assertThat(CleanupStatus.CANCELLED.isFinished()).isTrue();
    }

    @Test
    void testScheduleTypeValues() {
        // Test all enum values exist
        ScheduleType[] types = ScheduleType.values();
        assertThat(types).hasSize(5);
        assertThat(types).contains(
            ScheduleType.ONCE,
            ScheduleType.DAILY,
            ScheduleType.WEEKLY,
            ScheduleType.MONTHLY,
            ScheduleType.CRON
        );
    }

    @Test
    void testScheduleTypeValueAndDescription() {
        // Test ONCE
        assertThat(ScheduleType.ONCE.getValue()).isEqualTo("once");
        assertThat(ScheduleType.ONCE.getDescription()).isEqualTo("执行一次");

        // Test DAILY
        assertThat(ScheduleType.DAILY.getValue()).isEqualTo("daily");
        assertThat(ScheduleType.DAILY.getDescription()).isEqualTo("每日执行");

        // Test WEEKLY
        assertThat(ScheduleType.WEEKLY.getValue()).isEqualTo("weekly");
        assertThat(ScheduleType.WEEKLY.getDescription()).isEqualTo("每周执行");

        // Test MONTHLY
        assertThat(ScheduleType.MONTHLY.getValue()).isEqualTo("monthly");
        assertThat(ScheduleType.MONTHLY.getDescription()).isEqualTo("每月执行");

        // Test CRON
        assertThat(ScheduleType.CRON.getValue()).isEqualTo("cron");
        assertThat(ScheduleType.CRON.getDescription()).isEqualTo("CRON表达式");
    }

    @Test
    void testScheduleTypeFromValue() {
        // Test valid values
        assertThat(ScheduleType.fromValue("once")).isEqualTo(ScheduleType.ONCE);
        assertThat(ScheduleType.fromValue("daily")).isEqualTo(ScheduleType.DAILY);
        assertThat(ScheduleType.fromValue("weekly")).isEqualTo(ScheduleType.WEEKLY);
        assertThat(ScheduleType.fromValue("monthly")).isEqualTo(ScheduleType.MONTHLY);
        assertThat(ScheduleType.fromValue("cron")).isEqualTo(ScheduleType.CRON);
    }

    @Test
    void testScheduleTypeFromValueInvalid() {
        // Test invalid value
        assertThatThrownBy(() -> ScheduleType.fromValue("invalid"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unknown schedule type: invalid");
    }

    @Test
    void testScheduleTypeRequiresCronExpression() {
        // Test CRON requires expression
        assertThat(ScheduleType.CRON.requiresCronExpression()).isTrue();

        // Test others don't require expression
        assertThat(ScheduleType.ONCE.requiresCronExpression()).isFalse();
        assertThat(ScheduleType.DAILY.requiresCronExpression()).isFalse();
        assertThat(ScheduleType.WEEKLY.requiresCronExpression()).isFalse();
        assertThat(ScheduleType.MONTHLY.requiresCronExpression()).isFalse();
    }

    @Test
    void testScheduleTypeIsSimpleSchedule() {
        // Test simple schedule types
        assertThat(ScheduleType.DAILY.isSimpleSchedule()).isTrue();
        assertThat(ScheduleType.WEEKLY.isSimpleSchedule()).isTrue();
        assertThat(ScheduleType.MONTHLY.isSimpleSchedule()).isTrue();

        // Test non-simple schedule types
        assertThat(ScheduleType.ONCE.isSimpleSchedule()).isFalse();
        assertThat(ScheduleType.CRON.isSimpleSchedule()).isFalse();
    }
}