package cloud.xcan.angus.core.repo.domain.cleanup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

/**
 * 清理策略实体单元测试
 * 
 * 测试实体类的基本功能和约束
 * 需求: 需求 1.1, 3.1
 */
public class CleanupPolicyTest {

    private CleanupPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new CleanupPolicy();
    }

    @Test
    void testBasicProperties() {
        // Given
        String id = "policy-001";
        String name = "Test Policy";
        String description = "Test Description";
        String repositoryId = "repo-001";
        CleanupType type = CleanupType.BY_AGE;

        // When
        policy.setId(id)
               .setName(name)
               .setDescription(description)
               .setRepositoryId(repositoryId)
               .setType(type)
               .setEnabled(true)
               .setDryRun(false);

        // Then
        assertThat(policy.getId()).isEqualTo(id);
        assertThat(policy.getName()).isEqualTo(name);
        assertThat(policy.getDescription()).isEqualTo(description);
        assertThat(policy.getRepositoryId()).isEqualTo(repositoryId);
        assertThat(policy.getType()).isEqualTo(type);
        assertThat(policy.getEnabled()).isTrue();
        assertThat(policy.getDryRun()).isFalse();
        assertThat(policy.identity()).isEqualTo(id);
    }

    @Test
    void testDefaultValues() {
        // Given: 新创建的策略实体
        CleanupPolicy newPolicy = new CleanupPolicy();

        // Then: 验证默认值
        assertThat(newPolicy.getEnabled()).isTrue();
        assertThat(newPolicy.getDryRun()).isFalse();
        assertThat(newPolicy.getExecutionCount()).isEqualTo(0);
    }

    @Test
    void testAuditFields() {
        // Given
        Long createdBy = 1001L;
        Long modifiedBy = 1002L;
        LocalDateTime now = LocalDateTime.now();

        // When
        policy.setCreatedBy(createdBy)
               .setCreatedDate(now)
               .setModifiedBy(modifiedBy)
               .setModifiedDate(now);

        // Then
        assertThat(policy.getCreatedBy()).isEqualTo(createdBy);
        assertThat(policy.getCreatedDate()).isEqualTo(now);
        assertThat(policy.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(policy.getModifiedDate()).isEqualTo(now);
    }

    @Test
    void testExecutionStatistics() {
        // Given
        LocalDateTime lastExecuted = LocalDateTime.now();
        LocalDateTime nextExecution = LocalDateTime.now().plusHours(24);
        Integer executionCount = 5;

        // When
        policy.setLastExecuted(lastExecuted)
               .setNextExecution(nextExecution)
               .setExecutionCount(executionCount);

        // Then
        assertThat(policy.getLastExecuted()).isEqualTo(lastExecuted);
        assertThat(policy.getNextExecution()).isEqualTo(nextExecution);
        assertThat(policy.getExecutionCount()).isEqualTo(executionCount);
    }

    @Test
    void testTransientFields() {
        // Given
        String repositoryName = "Test Repository";
        CleanupPolicy.UserInfo creator = new CleanupPolicy.UserInfo(1001L, "Creator", "creator@test.com");
        CleanupPolicy.UserInfo modifier = new CleanupPolicy.UserInfo(1002L, "Modifier", "modifier@test.com");

        // When
        policy.setRepositoryName(repositoryName)
               .setCreator(creator)
               .setModifier(modifier);

        // Then
        assertThat(policy.getRepositoryName()).isEqualTo(repositoryName);
        assertThat(policy.getCreator()).isEqualTo(creator);
        assertThat(policy.getModifier()).isEqualTo(modifier);
    }

    @Test
    void testUserInfoInnerClass() {
        // Given
        Long id = 1001L;
        String name = "Test User";
        String email = "test@example.com";

        // When
        CleanupPolicy.UserInfo userInfo = new CleanupPolicy.UserInfo(id, name, email);

        // Then
        assertThat(userInfo.getId()).isEqualTo(id);
        assertThat(userInfo.getName()).isEqualTo(name);
        assertThat(userInfo.getEmail()).isEqualTo(email);
    }

    @Test
    void testUserInfoDefaultConstructor() {
        // Given & When
        CleanupPolicy.UserInfo userInfo = new CleanupPolicy.UserInfo();

        // Then
        assertThat(userInfo.getId()).isNull();
        assertThat(userInfo.getName()).isNull();
        assertThat(userInfo.getEmail()).isNull();
    }

    @Test
    void testUserInfoSetters() {
        // Given
        CleanupPolicy.UserInfo userInfo = new CleanupPolicy.UserInfo();
        Long id = 1001L;
        String name = "Test User";
        String email = "test@example.com";

        // When
        userInfo.setId(id);
        userInfo.setName(name);
        userInfo.setEmail(email);

        // Then
        assertThat(userInfo.getId()).isEqualTo(id);
        assertThat(userInfo.getName()).isEqualTo(name);
        assertThat(userInfo.getEmail()).isEqualTo(email);
    }

    @Test
    void testChainedSetters() {
        // Given
        String id = "policy-001";
        String name = "Test Policy";
        CleanupType type = CleanupType.BY_COUNT;

        // When: 使用链式调用
        CleanupPolicy result = policy.setId(id)
                                    .setName(name)
                                    .setType(type)
                                    .setEnabled(false);

        // Then: 验证链式调用返回同一个对象
        assertThat(result).isSameAs(policy);
        assertThat(policy.getId()).isEqualTo(id);
        assertThat(policy.getName()).isEqualTo(name);
        assertThat(policy.getType()).isEqualTo(type);
        assertThat(policy.getEnabled()).isFalse();
    }

    @Test
    void testNullValues() {
        // When: 设置null值
        policy.setDescription(null)
               .setLastExecuted(null)
               .setNextExecution(null)
               .setRepositoryName(null)
               .setCreator(null)
               .setModifier(null);

        // Then: 验证null值的处理
        assertThat(policy.getDescription()).isNull();
        assertThat(policy.getLastExecuted()).isNull();
        assertThat(policy.getNextExecution()).isNull();
        assertThat(policy.getRepositoryName()).isNull();
        assertThat(policy.getCreator()).isNull();
        assertThat(policy.getModifier()).isNull();
    }

    @Test
    void testIdentityMethod() {
        // Given
        String id = "test-policy-id";
        policy.setId(id);

        // When & Then
        assertThat(policy.identity()).isEqualTo(id);
    }

    @Test
    void testIdentityMethodWithNullId() {
        // Given: ID为null的策略
        policy.setId(null);

        // When & Then
        assertThat(policy.identity()).isNull();
    }
}