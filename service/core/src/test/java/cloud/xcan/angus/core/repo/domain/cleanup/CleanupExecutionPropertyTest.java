package cloud.xcan.angus.core.repo.domain.cleanup;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 清理执行记录领域层属性测试
 * 
 * 验证清理执行记录的生命周期管理和状态转换
 */
public class CleanupExecutionPropertyTest {

    /**
     * 属性测试：执行记录基本属性一致性
     * 验证执行记录创建后，所有字段都能正确设置和获取
     */
    @Property(tries = 100)
    void cleanupExecutionBasicPropertiesConsistency(
            @ForAll @AlphaChars @StringLength(min = 1, max = 64) String id,
            @ForAll @AlphaChars @StringLength(min = 1, max = 64) String policyId,
            @ForAll CleanupStatus status,
            @ForAll @IntRange(min = 0, max = 100) Integer progress) {

        // Given: 创建清理执行记录实体
        CleanupExecution execution = new CleanupExecution()
                .setId(id)
                .setPolicyId(policyId)
                .setStatus(status)
                .setProgress(progress);

        // Then: 验证所有属性都能正确设置和获取
        assertThat(execution.getId()).isEqualTo(id);
        assertThat(execution.getPolicyId()).isEqualTo(policyId);
        assertThat(execution.getStatus()).isEqualTo(status);
        assertThat(execution.getProgress()).isEqualTo(progress);
        assertThat(execution.identity()).isEqualTo(id);
    }

    /**
     * 属性测试：执行状态转换的一致性
     * 验证执行状态的转换逻辑
     */
    @Property(tries = 100)
    void executionStatusTransitionConsistency(@ForAll CleanupStatus status) {
        // Given: 创建执行记录并设置状态
        CleanupExecution execution = new CleanupExecution().setStatus(status);

        // Then: 验证状态判断方法的一致性
        boolean expectedRunning = (status == CleanupStatus.RUNNING || status == CleanupStatus.PENDING);
        boolean expectedFinished = (status == CleanupStatus.COMPLETED || 
                                   status == CleanupStatus.FAILED || 
                                   status == CleanupStatus.CANCELLED);

        assertThat(execution.isRunning()).isEqualTo(expectedRunning);
        assertThat(execution.isFinished()).isEqualTo(expectedFinished);
        
        // 运行中和已完成状态应该是互斥的
        if (status != CleanupStatus.PENDING) { // PENDING状态比较特殊，既不算运行中也不算完成
            assertThat(execution.isRunning() && execution.isFinished()).isFalse();
        }
    }

    /**
     * 属性测试：执行时长计算的一致性
     * 验证执行时长的计算逻辑
     */
    @Property(tries = 100)
    void executionDurationCalculationConsistency(
            @ForAll @IntRange(min = 1, max = 3600) Integer durationSeconds) {

        // Given: 创建执行记录并设置开始和结束时间
        CleanupExecution execution = new CleanupExecution();
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusSeconds(durationSeconds);
        
        execution.setStartTime(startTime);
        execution.setEndTime(endTime);

        // When: 计算执行时长
        Long calculatedDuration = execution.calculateDurationSeconds();

        // Then: 验证计算结果的一致性
        assertThat(calculatedDuration).isNotNull();
        assertThat(calculatedDuration).isEqualTo(durationSeconds.longValue());
    }

    /**
     * 属性测试：执行时长计算边界情况
     * 验证没有开始时间或结束时间时的处理
     */
    @Property(tries = 100)
    void executionDurationCalculationBoundaryConditions(
            @ForAll boolean hasStartTime,
            @ForAll boolean hasEndTime) {

        // Given: 创建执行记录
        CleanupExecution execution = new CleanupExecution();
        
        if (hasStartTime) {
            execution.setStartTime(LocalDateTime.now());
        }
        if (hasEndTime) {
            execution.setEndTime(LocalDateTime.now().plusMinutes(10));
        }

        // When: 计算执行时长
        Long calculatedDuration = execution.calculateDurationSeconds();

        // Then: 验证边界情况的处理
        if (hasStartTime && hasEndTime) {
            assertThat(calculatedDuration).isNotNull();
            assertThat(calculatedDuration).isGreaterThanOrEqualTo(0L);
        } else {
            assertThat(calculatedDuration).isNull();
        }
    }

    /**
     * 属性测试：执行进度的有效性
     * 验证执行进度的取值范围
     */
    @Property(tries = 100)
    void executionProgressValidation(@ForAll @IntRange(min = -10, max = 110) Integer progress) {
        // Given: 创建执行记录并设置进度
        CleanupExecution execution = new CleanupExecution().setProgress(progress);

        // Then: 验证进度值（虽然实体本身不做验证，但我们可以验证设置是否成功）
        assertThat(execution.getProgress()).isEqualTo(progress);
        
        // 在实际业务中，进度应该在0-100之间，但这里只验证设置的一致性
        // 业务验证应该在服务层进行
    }

    /**
     * 属性测试：错误消息的处理
     * 验证错误消息的设置和获取
     */
    @Property(tries = 100)
    void errorMessageHandling(
            @ForAll @StringLength(max = 4000) String errorMessage) {

        // Given: 创建执行记录并设置错误消息
        CleanupExecution execution = new CleanupExecution().setErrorMessage(errorMessage);

        // Then: 验证错误消息的一致性
        assertThat(execution.getErrorMessage()).isEqualTo(errorMessage);
    }

    /**
     * 属性测试：创建时间的自动设置
     * 验证PrePersist方法的行为
     */
    @Property(tries = 100)
    void createdDateAutoSetting() {
        // Given: 创建执行记录
        CleanupExecution execution = new CleanupExecution();
        LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);

        // When: 调用PrePersist方法
        execution.onCreate();
        LocalDateTime afterCreate = LocalDateTime.now().plusSeconds(1);

        // Then: 验证创建时间被正确设置
        assertThat(execution.getCreatedDate()).isNotNull();
        assertThat(execution.getCreatedDate()).isAfter(beforeCreate);
        assertThat(execution.getCreatedDate()).isBefore(afterCreate);
    }

    // 生成器方法
    @Provide
    Arbitrary<CleanupStatus> cleanupStatus() {
        return Arbitraries.of(CleanupStatus.values());
    }

    /**
     * 单元测试：验证状态枚举的业务方法
     */
    @Test
    void testCleanupStatusBusinessMethods() {
        // 验证运行中状态
        assertThat(CleanupStatus.PENDING.isRunning()).isTrue();
        assertThat(CleanupStatus.RUNNING.isRunning()).isTrue();
        assertThat(CleanupStatus.COMPLETED.isRunning()).isFalse();
        assertThat(CleanupStatus.FAILED.isRunning()).isFalse();
        assertThat(CleanupStatus.CANCELLED.isRunning()).isFalse();

        // 验证完成状态
        assertThat(CleanupStatus.PENDING.isFinished()).isFalse();
        assertThat(CleanupStatus.RUNNING.isFinished()).isFalse();
        assertThat(CleanupStatus.COMPLETED.isFinished()).isTrue();
        assertThat(CleanupStatus.FAILED.isFinished()).isTrue();
        assertThat(CleanupStatus.CANCELLED.isFinished()).isTrue();
    }

    /**
     * 单元测试：验证执行记录的默认值
     */
    @Test
    void testCleanupExecutionDefaults() {
        // Given: 创建新的执行记录
        CleanupExecution execution = new CleanupExecution();

        // Then: 验证默认值
        assertThat(execution.getStatus()).isEqualTo(CleanupStatus.PENDING);
        assertThat(execution.getProgress()).isEqualTo(0);
    }

    /**
     * 单元测试：验证时间相关的边界情况
     */
    @Test
    void testTimeRelatedBoundaryCases() {
        CleanupExecution execution = new CleanupExecution();

        // 测试相同的开始和结束时间
        LocalDateTime now = LocalDateTime.now();
        execution.setStartTime(now);
        execution.setEndTime(now);
        
        assertThat(execution.calculateDurationSeconds()).isEqualTo(0L);

        // 测试结束时间早于开始时间的情况
        execution.setEndTime(now.minusMinutes(1));
        Long duration = execution.calculateDurationSeconds();
        assertThat(duration).isNegative();
    }
}