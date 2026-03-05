package cloud.xcan.angus.core.repo.interfaces.cleanup;

import static org.assertj.core.api.Assertions.assertThat;

import cloud.xcan.angus.core.repo.domain.cleanup.CleanupExecution;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicy;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupStatus;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupType;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto.CleanupPolicyBatchDeleteDto;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto.CleanupPolicyCreateDto;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto.CleanupPolicyFindDto;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto.CleanupPolicyUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.internal.assembler.CleanupAssembler;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.vo.CleanupExecutionVo;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.vo.CleanupPolicyDetailVo;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.vo.CleanupStatisticsVo;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * 清理模块DTO/VO/Assembler单元测试
 *
 * 测试接口层的数据传输对象、视图对象和组装器
 */
public class CleanupDtoVoTest {

    // ==================== DTO Tests ====================

    @Test
    void testCleanupPolicyCreateDto() {
        // Given
        String name = "Age-based Cleanup";
        String description = "Remove old artifacts";
        String repositoryId = "repo-001";
        CleanupType type = CleanupType.BY_AGE;
        Boolean enabled = true;
        Boolean dryRun = false;
        String conditionJson = "{\"maxAge\":30}";
        String scheduleJson = "{\"cron\":\"0 0 * * *\"}";

        // When
        CleanupPolicyCreateDto dto = new CleanupPolicyCreateDto();
        CleanupPolicyCreateDto result = dto.setName(name)
                .setDescription(description)
                .setRepositoryId(repositoryId)
                .setType(type)
                .setEnabled(enabled)
                .setDryRun(dryRun)
                .setConditionJson(conditionJson)
                .setScheduleJson(scheduleJson);

        // Then
        assertThat(result).isSameAs(dto);
        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getDescription()).isEqualTo(description);
        assertThat(dto.getRepositoryId()).isEqualTo(repositoryId);
        assertThat(dto.getType()).isEqualTo(type);
        assertThat(dto.getEnabled()).isEqualTo(enabled);
        assertThat(dto.getDryRun()).isEqualTo(dryRun);
        assertThat(dto.getConditionJson()).isEqualTo(conditionJson);
        assertThat(dto.getScheduleJson()).isEqualTo(scheduleJson);
    }

    @Test
    void testCleanupPolicyUpdateDto() {
        // Given
        String name = "Updated Policy";
        CleanupType type = CleanupType.BY_COUNT;
        Boolean enabled = false;

        // When: partial update - some fields set, others left null
        CleanupPolicyUpdateDto dto = new CleanupPolicyUpdateDto();
        CleanupPolicyUpdateDto result = dto.setName(name)
                .setType(type)
                .setEnabled(enabled);

        // Then
        assertThat(result).isSameAs(dto);
        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getType()).isEqualTo(type);
        assertThat(dto.getEnabled()).isEqualTo(enabled);
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getRepositoryId()).isNull();
        assertThat(dto.getDryRun()).isNull();
        assertThat(dto.getConditionJson()).isNull();
        assertThat(dto.getScheduleJson()).isNull();
    }

    @Test
    void testCleanupPolicyFindDto() {
        // Given
        String repositoryId = "repo-001";
        CleanupType type = CleanupType.BY_SIZE;
        Boolean enabled = true;
        String search = "cleanup";

        // When
        CleanupPolicyFindDto dto = new CleanupPolicyFindDto();
        dto.setRepositoryId(repositoryId);
        dto.setType(type);
        dto.setEnabled(enabled);
        dto.setSearch(search);

        // Then
        assertThat(dto.getRepositoryId()).isEqualTo(repositoryId);
        assertThat(dto.getType()).isEqualTo(type);
        assertThat(dto.getEnabled()).isEqualTo(enabled);
        assertThat(dto.getSearch()).isEqualTo(search);
        assertThat(dto.getDefaultOrderBy()).isEqualTo("createdDate");
    }

    @Test
    void testCleanupPolicyBatchDeleteDto() {
        // Given
        List<String> ids = Arrays.asList("id-001", "id-002", "id-003");

        // When
        CleanupPolicyBatchDeleteDto dto = new CleanupPolicyBatchDeleteDto();
        CleanupPolicyBatchDeleteDto result = dto.setIds(ids);

        // Then
        assertThat(result).isSameAs(dto);
        assertThat(dto.getIds()).hasSize(3);
        assertThat(dto.getIds()).containsExactly("id-001", "id-002", "id-003");
    }

    // ==================== VO Tests ====================

    @Test
    void testCleanupPolicyDetailVo() {
        // Given
        String id = "policy-001";
        String name = "Detail Policy";
        String description = "Policy description";
        String repositoryId = "repo-001";
        String repositoryName = "Test Repository";
        CleanupType type = CleanupType.BY_PATTERN;
        Boolean enabled = true;
        Boolean dryRun = false;
        String conditionJson = "{\"pattern\":\"*.tmp\"}";
        String scheduleJson = "{\"cron\":\"0 0 * * *\"}";
        String lastExecutionStatsJson = "{\"deleted\":10}";
        LocalDateTime lastExecuted = LocalDateTime.of(2024, 1, 15, 10, 30);
        LocalDateTime nextExecution = LocalDateTime.of(2024, 1, 16, 10, 30);
        Integer executionCount = 5;
        Long createdBy = 1001L;
        LocalDateTime createdDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        Long modifiedBy = 1002L;
        LocalDateTime modifiedDate = LocalDateTime.of(2024, 1, 10, 12, 0);

        // When
        CleanupPolicyDetailVo vo = new CleanupPolicyDetailVo();
        CleanupPolicyDetailVo result = vo.setId(id)
                .setName(name)
                .setDescription(description)
                .setRepositoryId(repositoryId)
                .setRepositoryName(repositoryName)
                .setType(type)
                .setEnabled(enabled)
                .setDryRun(dryRun)
                .setConditionJson(conditionJson)
                .setScheduleJson(scheduleJson)
                .setLastExecutionStatsJson(lastExecutionStatsJson)
                .setLastExecuted(lastExecuted)
                .setNextExecution(nextExecution)
                .setExecutionCount(executionCount)
                .setCreatedBy(createdBy)
                .setCreatedDate(createdDate)
                .setModifiedBy(modifiedBy)
                .setModifiedDate(modifiedDate);

        // Then
        assertThat(result).isSameAs(vo);
        assertThat(vo.getId()).isEqualTo(id);
        assertThat(vo.getName()).isEqualTo(name);
        assertThat(vo.getDescription()).isEqualTo(description);
        assertThat(vo.getRepositoryId()).isEqualTo(repositoryId);
        assertThat(vo.getRepositoryName()).isEqualTo(repositoryName);
        assertThat(vo.getType()).isEqualTo(type);
        assertThat(vo.getEnabled()).isTrue();
        assertThat(vo.getDryRun()).isFalse();
        assertThat(vo.getConditionJson()).isEqualTo(conditionJson);
        assertThat(vo.getScheduleJson()).isEqualTo(scheduleJson);
        assertThat(vo.getLastExecutionStatsJson()).isEqualTo(lastExecutionStatsJson);
        assertThat(vo.getLastExecuted()).isEqualTo(lastExecuted);
        assertThat(vo.getNextExecution()).isEqualTo(nextExecution);
        assertThat(vo.getExecutionCount()).isEqualTo(executionCount);
        assertThat(vo.getCreatedBy()).isEqualTo(createdBy);
        assertThat(vo.getCreatedDate()).isEqualTo(createdDate);
        assertThat(vo.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(vo.getModifiedDate()).isEqualTo(modifiedDate);
    }

    @Test
    void testCleanupExecutionVo() {
        // Given
        String id = "exec-001";
        String policyId = "policy-001";
        String policyName = "Test Policy";
        CleanupStatus status = CleanupStatus.COMPLETED;
        Integer progress = 100;
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 15, 10, 30);
        Long durationSeconds = 1800L;
        String errorMessage = null;
        String statisticsJson = "{\"deleted\":50}";
        LocalDateTime createdDate = LocalDateTime.of(2024, 1, 15, 9, 55);

        // When
        CleanupExecutionVo vo = new CleanupExecutionVo();
        CleanupExecutionVo result = vo.setId(id)
                .setPolicyId(policyId)
                .setPolicyName(policyName)
                .setStatus(status)
                .setProgress(progress)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setDurationSeconds(durationSeconds)
                .setErrorMessage(errorMessage)
                .setStatisticsJson(statisticsJson)
                .setCreatedDate(createdDate);

        // Then
        assertThat(result).isSameAs(vo);
        assertThat(vo.getId()).isEqualTo(id);
        assertThat(vo.getPolicyId()).isEqualTo(policyId);
        assertThat(vo.getPolicyName()).isEqualTo(policyName);
        assertThat(vo.getStatus()).isEqualTo(status);
        assertThat(vo.getProgress()).isEqualTo(progress);
        assertThat(vo.getStartTime()).isEqualTo(startTime);
        assertThat(vo.getEndTime()).isEqualTo(endTime);
        assertThat(vo.getDurationSeconds()).isEqualTo(durationSeconds);
        assertThat(vo.getErrorMessage()).isNull();
        assertThat(vo.getStatisticsJson()).isEqualTo(statisticsJson);
        assertThat(vo.getCreatedDate()).isEqualTo(createdDate);
    }

    @Test
    void testCleanupStatisticsVo() {
        // Given
        Long totalPolicies = 10L;
        Long enabledPolicies = 7L;
        Long totalExecutions = 100L;
        Long completedExecutions = 85L;
        Long failedExecutions = 15L;
        Long totalDeletedArtifacts = 5000L;
        Long totalFreedSpaceBytes = 1073741824L;

        // When
        CleanupStatisticsVo vo = new CleanupStatisticsVo();
        CleanupStatisticsVo result = vo.setTotalPolicies(totalPolicies)
                .setEnabledPolicies(enabledPolicies)
                .setTotalExecutions(totalExecutions)
                .setCompletedExecutions(completedExecutions)
                .setFailedExecutions(failedExecutions)
                .setTotalDeletedArtifacts(totalDeletedArtifacts)
                .setTotalFreedSpaceBytes(totalFreedSpaceBytes);

        // Then
        assertThat(result).isSameAs(vo);
        assertThat(vo.getTotalPolicies()).isEqualTo(totalPolicies);
        assertThat(vo.getEnabledPolicies()).isEqualTo(enabledPolicies);
        assertThat(vo.getTotalExecutions()).isEqualTo(totalExecutions);
        assertThat(vo.getCompletedExecutions()).isEqualTo(completedExecutions);
        assertThat(vo.getFailedExecutions()).isEqualTo(failedExecutions);
        assertThat(vo.getTotalDeletedArtifacts()).isEqualTo(totalDeletedArtifacts);
        assertThat(vo.getTotalFreedSpaceBytes()).isEqualTo(totalFreedSpaceBytes);
    }

    // ==================== Assembler Tests ====================

    @Test
    void testToCreateEntity() {
        // Given
        CleanupPolicyCreateDto dto = new CleanupPolicyCreateDto();
        dto.setName("New Policy")
                .setDescription("New description")
                .setRepositoryId("repo-001")
                .setType(CleanupType.BY_AGE)
                .setEnabled(true)
                .setDryRun(false)
                .setConditionJson("{\"maxAge\":30}")
                .setScheduleJson("{\"cron\":\"0 0 * * *\"}");

        // When
        CleanupPolicy entity = CleanupAssembler.toCreateEntity(dto);

        // Then
        assertThat(entity.getId()).isNotNull().isNotEmpty();
        assertThat(entity.getName()).isEqualTo("New Policy");
        assertThat(entity.getDescription()).isEqualTo("New description");
        assertThat(entity.getRepositoryId()).isEqualTo("repo-001");
        assertThat(entity.getType()).isEqualTo(CleanupType.BY_AGE);
        assertThat(entity.getEnabled()).isTrue();
        assertThat(entity.getDryRun()).isFalse();
        assertThat(entity.getConditionJson()).isEqualTo("{\"maxAge\":30}");
        assertThat(entity.getScheduleJson()).isEqualTo("{\"cron\":\"0 0 * * *\"}");
    }

    @Test
    void testToUpdateEntity() {
        // Given
        CleanupPolicyUpdateDto dto = new CleanupPolicyUpdateDto();
        dto.setName("Updated Policy")
                .setEnabled(false)
                .setType(CleanupType.BY_COUNT);
        String id = "test-id";

        // When
        CleanupPolicy entity = CleanupAssembler.toUpdateEntity(dto, id);

        // Then
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo("Updated Policy");
        assertThat(entity.getEnabled()).isFalse();
        assertThat(entity.getType()).isEqualTo(CleanupType.BY_COUNT);
        assertThat(entity.getDescription()).isNull();
        assertThat(entity.getRepositoryId()).isNull();
        assertThat(entity.getDryRun()).isNull();
        assertThat(entity.getConditionJson()).isNull();
        assertThat(entity.getScheduleJson()).isNull();
    }

    @Test
    void testToPolicyDetailVo() {
        // Given
        LocalDateTime now = LocalDateTime.of(2024, 1, 15, 10, 30);
        CleanupPolicy entity = new CleanupPolicy();
        entity.setId("policy-001")
                .setName("Test Policy")
                .setDescription("Test description")
                .setRepositoryId("repo-001")
                .setRepositoryName("Test Repository")
                .setType(CleanupType.BY_SIZE)
                .setEnabled(true)
                .setDryRun(false)
                .setConditionJson("{\"maxSize\":1024}")
                .setScheduleJson("{\"cron\":\"0 0 * * *\"}")
                .setLastExecutionStatsJson("{\"deleted\":5}")
                .setLastExecuted(now)
                .setNextExecution(now.plusHours(24))
                .setExecutionCount(3)
                .setCreatedBy(1001L)
                .setCreatedDate(now.minusDays(10))
                .setModifiedBy(1002L)
                .setModifiedDate(now.minusDays(1));

        // When
        CleanupPolicyDetailVo vo = CleanupAssembler.toPolicyDetailVo(entity);

        // Then
        assertThat(vo.getId()).isEqualTo("policy-001");
        assertThat(vo.getName()).isEqualTo("Test Policy");
        assertThat(vo.getDescription()).isEqualTo("Test description");
        assertThat(vo.getRepositoryId()).isEqualTo("repo-001");
        assertThat(vo.getRepositoryName()).isEqualTo("Test Repository");
        assertThat(vo.getType()).isEqualTo(CleanupType.BY_SIZE);
        assertThat(vo.getEnabled()).isTrue();
        assertThat(vo.getDryRun()).isFalse();
        assertThat(vo.getConditionJson()).isEqualTo("{\"maxSize\":1024}");
        assertThat(vo.getScheduleJson()).isEqualTo("{\"cron\":\"0 0 * * *\"}");
        assertThat(vo.getLastExecutionStatsJson()).isEqualTo("{\"deleted\":5}");
        assertThat(vo.getLastExecuted()).isEqualTo(now);
        assertThat(vo.getNextExecution()).isEqualTo(now.plusHours(24));
        assertThat(vo.getExecutionCount()).isEqualTo(3);
        assertThat(vo.getCreatedBy()).isEqualTo(1001L);
        assertThat(vo.getCreatedDate()).isEqualTo(now.minusDays(10));
        assertThat(vo.getModifiedBy()).isEqualTo(1002L);
        assertThat(vo.getModifiedDate()).isEqualTo(now.minusDays(1));
    }

    @Test
    void testToExecutionVo() {
        // Given
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 15, 10, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        CleanupExecution entity = new CleanupExecution();
        entity.setId("exec-001")
                .setPolicyId("policy-001")
                .setPolicyName("Test Policy")
                .setStatus(CleanupStatus.COMPLETED)
                .setProgress(100)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setErrorMessage(null)
                .setStatisticsJson("{\"deleted\":50}")
                .setCreatedDate(startTime.minusMinutes(5));

        // When
        CleanupExecutionVo vo = CleanupAssembler.toExecutionVo(entity);

        // Then
        assertThat(vo.getId()).isEqualTo("exec-001");
        assertThat(vo.getPolicyId()).isEqualTo("policy-001");
        assertThat(vo.getPolicyName()).isEqualTo("Test Policy");
        assertThat(vo.getStatus()).isEqualTo(CleanupStatus.COMPLETED);
        assertThat(vo.getProgress()).isEqualTo(100);
        assertThat(vo.getStartTime()).isEqualTo(startTime);
        assertThat(vo.getEndTime()).isEqualTo(endTime);
        assertThat(vo.getDurationSeconds()).isEqualTo(1800L);
        assertThat(vo.getErrorMessage()).isNull();
        assertThat(vo.getStatisticsJson()).isEqualTo("{\"deleted\":50}");
        assertThat(vo.getCreatedDate()).isEqualTo(startTime.minusMinutes(5));
    }

    @Test
    void testNullEntityToVo() {
        // When & Then
        assertThat(CleanupAssembler.toPolicyDetailVo(null)).isNull();
        assertThat(CleanupAssembler.toExecutionVo(null)).isNull();
    }
}
