package cloud.xcan.angus.core.repo.domain.cleanup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * 清理执行记录实体单元测试
 * 
 * 测试实体类的基本功能和约束
 * 需求: 需求 1.1, 3.1
 */
public class CleanupExecutionTest {

    private CleanupExecution execution;

    @BeforeEach
    void setUp() {
        execution = new CleanupExecution();
    }

    @Test
    void testBasicProperties() {
        // Given
        String id = "execution-001";
        String policyId = "policy-001";
        CleanupStatus status = CleanupStatus.RUNNING;
        Integer progress = 50;

        // When
        execution.setId(id)
                 .setPolicyId(policyId)
                 .setStatus(status)
                 .setProgress(progress);

        // Then
        assertThat(execution.getId()).isEqualTo(id);
        assertThat(execution.getPolicyId()).isEqualTo(policyId);
        assertThat(execution.getStatus()).isEqualTo(status);
        assertThat(execution.getProgress()).isEqualTo(progress);
        assertThat(execution.identity()).isEqualTo(id);
    }

    @Test
    void testDefaultValues() {
        // Given: 新创建的执行记录实体
        CleanupExecution newExecution = new CleanupExecution();

        // Then: 验证默认值
        assertThat(newExecution.getStatus()).isEqualTo(CleanupStatus.PENDING);
        assertThat(newExecution.getProgress()).isEqualTo(0);
    }

    @Test
    void testTimeFields() {
        // Given
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(30);

        // When
        execution.setStartTime(startTime)
                 .setEndTime(endTime);

        // Then
        assertThat(execution.getStartTime()).isEqualTo(startTime);
        assertThat(execution.getEndTime()).isEqualTo(endTime);
    }

    @Test
    void testErrorMessage() {
        // Given
        String errorMessage = "Test error message";

        // When
        execution.setErrorMessage(errorMessage);

        // Then
        assertThat(execution.getErrorMessage()).isEqualTo(errorMessage);
    }

    @Test
    void testTransientFields() {
        // Given
        String policyName = "Test Policy";
        Long durationSeconds = 1800L;

        // When
        execution.setPolicyName(policyName)
                 .setDurationSeconds(durationSeconds);

        // Then
        assertThat(execution.getPolicyName()).isEqualTo(policyName);
        assertThat(execution.getDurationSeconds()).isEqualTo(durationSeconds);
    }

    @Test
    void testCalculateDurationSeconds() {
        // Given
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 1, 10, 30, 0);
        execution.setStartTime(startTime).setEndTime(endTime);

        // When
        Long duration = execution.calculateDurationSeconds();

        // Then
        assertThat(duration).isEqualTo(1800L); // 30 minutes = 1800 seconds
    }

    @Test
    void testCalculateDurationSecondsWithNullStartTime() {
        // Given
        execution.setStartTime(null)
                 .setEndTime(LocalDateTime.now());

        // When
        Long duration = execution.calculateDurationSeconds();

        // Then
        assertThat(duration).isNull();
    }

    @Test
    void testCalculateDurationSecondsWithNullEndTime() {
        // Given
        execution.setStartTime(LocalDateTime.now())
                 .setEndTime(null);

        // When
        Long duration = execution.calculateDurationSeconds();

        // Then
        assertThat(duration).isNull();
    }

    @Test
    void testCalculateDurationSecondsWithBothNull() {
        // Given
        execution.setStartTime(null)
                 .setEndTime(null);

        // When
        Long duration = execution.calculateDurationSeconds();

        // Then
        assertThat(duration).isNull();
    }

    @Test
    void testCalculateDurationSecondsWithSameTime() {
        // Given
        LocalDateTime time = LocalDateTime.now();
        execution.setStartTime(time)
                 .setEndTime(time);

        // When
        Long duration = execution.calculateDurationSeconds();

        // Then
        assertThat(duration).isEqualTo(0L);
    }

    @Test
    void testCalculateDurationSecondsWithEndBeforeStart() {
        // Given
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 10, 30, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        execution.setStartTime(startTime)
                 .setEndTime(endTime);

        // When
        Long duration = execution.calculateDurationSeconds();

        // Then
        assertThat(duration).isEqualTo(-1800L); // 负数时长
    }

    @Test
    void testIsRunning() {
        // Test PENDING status
        execution.setStatus(CleanupStatus.PENDING);
        assertThat(execution.isRunning()).isTrue();

        // Test RUNNING status
        execution.setStatus(CleanupStatus.RUNNING);
        assertThat(execution.isRunning()).isTrue();

        // Test COMPLETED status
        execution.setStatus(CleanupStatus.COMPLETED);
        assertThat(execution.isRunning()).isFalse();

        // Test FAILED status
        execution.setStatus(CleanupStatus.FAILED);
        assertThat(execution.isRunning()).isFalse();

        // Test CANCELLED status
        execution.setStatus(CleanupStatus.CANCELLED);
        assertThat(execution.isRunning()).isFalse();
    }

    @Test
    void testIsFinished() {
        // Test PENDING status
        execution.setStatus(CleanupStatus.PENDING);
        assertThat(execution.isFinished()).isFalse();

        // Test RUNNING status
        execution.setStatus(CleanupStatus.RUNNING);
        assertThat(execution.isFinished()).isFalse();

        // Test COMPLETED status
        execution.setStatus(CleanupStatus.COMPLETED);
        assertThat(execution.isFinished()).isTrue();

        // Test FAILED status
        execution.setStatus(CleanupStatus.FAILED);
        assertThat(execution.isFinished()).isTrue();

        // Test CANCELLED status
        execution.setStatus(CleanupStatus.CANCELLED);
        assertThat(execution.isFinished()).isTrue();
    }

    @Test
    void testOnCreate() {
        // Given
        LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);

        // When
        execution.onCreate();
        LocalDateTime afterCreate = LocalDateTime.now().plusSeconds(1);

        // Then
        assertThat(execution.getCreatedDate()).isNotNull();
        assertThat(execution.getCreatedDate()).isAfter(beforeCreate);
        assertThat(execution.getCreatedDate()).isBefore(afterCreate);
    }

    @Test
    void testChainedSetters() {
        // Given
        String id = "execution-001";
        String policyId = "policy-001";
        CleanupStatus status = CleanupStatus.RUNNING;

        // When: 使用链式调用
        CleanupExecution result = execution.setId(id)
                                          .setPolicyId(policyId)
                                          .setStatus(status);

        // Then: 验证链式调用返回同一个对象
        assertThat(result).isSameAs(execution);
        assertThat(execution.getId()).isEqualTo(id);
        assertThat(execution.getPolicyId()).isEqualTo(policyId);
        assertThat(execution.getStatus()).isEqualTo(status);
    }

    @Test
    void testNullValues() {
        // When: 设置null值
        execution.setErrorMessage(null)
                 .setStartTime(null)
                 .setEndTime(null)
                 .setPolicyName(null)
                 .setDurationSeconds(null);

        // Then: 验证null值的处理
        assertThat(execution.getErrorMessage()).isNull();
        assertThat(execution.getStartTime()).isNull();
        assertThat(execution.getEndTime()).isNull();
        assertThat(execution.getPolicyName()).isNull();
        assertThat(execution.getDurationSeconds()).isNull();
    }

    @Test
    void testIdentityMethod() {
        // Given
        String id = "test-execution-id";
        execution.setId(id);

        // When & Then
        assertThat(execution.identity()).isEqualTo(id);
    }

    @Test
    void testIdentityMethodWithNullId() {
        // Given: ID为null的执行记录
        execution.setId(null);

        // When & Then
        assertThat(execution.identity()).isNull();
    }
}