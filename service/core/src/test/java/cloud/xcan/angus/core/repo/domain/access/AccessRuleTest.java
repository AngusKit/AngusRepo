package cloud.xcan.angus.core.repo.domain.access;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * AccessRule entity unit tests.
 */
public class AccessRuleTest {

    private AccessRule rule;

    @BeforeEach
    void setUp() {
        rule = new AccessRule();
    }

    @Test
    void testBasicProperties() {
        // Given
        Long id = 1L;
        Long repositoryId = 100L;
        String name = "Read-Only Rule";
        String description = "Allow read access";
        AccessPrincipalType principalType = AccessPrincipalType.USER;
        String principalId = "user-001";
        String permissions = "[\"read\"]";
        String paths = "[\"/com/example/**\"]";

        // When
        rule.setId(id)
            .setRepositoryId(repositoryId)
            .setName(name)
            .setDescription(description)
            .setPrincipalType(principalType)
            .setPrincipalId(principalId)
            .setPermissions(permissions)
            .setPaths(paths);

        // Then
        assertThat(rule.getId()).isEqualTo(id);
        assertThat(rule.getRepositoryId()).isEqualTo(repositoryId);
        assertThat(rule.getName()).isEqualTo(name);
        assertThat(rule.getDescription()).isEqualTo(description);
        assertThat(rule.getPrincipalType()).isEqualTo(principalType);
        assertThat(rule.getPrincipalId()).isEqualTo(principalId);
        assertThat(rule.getPermissions()).isEqualTo(permissions);
        assertThat(rule.getPaths()).isEqualTo(paths);
    }

    @Test
    void testDefaultValues() {
        // Given: a newly created access rule entity
        AccessRule newRule = new AccessRule();

        // Then: verify default values
        assertThat(newRule.getEnabled()).isTrue();
        assertThat(newRule.getPriority()).isEqualTo(0);
    }

    @Test
    void testAuditFields() {
        // Given
        Long createdBy = 1001L;
        Long modifiedBy = 1002L;
        LocalDateTime now = LocalDateTime.now();

        // When
        rule.setCreatedBy(createdBy)
            .setCreatedDate(now)
            .setModifiedBy(modifiedBy)
            .setModifiedDate(now);

        // Then
        assertThat(rule.getCreatedBy()).isEqualTo(createdBy);
        assertThat(rule.getCreatedDate()).isEqualTo(now);
        assertThat(rule.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(rule.getModifiedDate()).isEqualTo(now);
    }

    @Test
    void testTransientFields() {
        // Given
        String principalName = "John Doe";

        // When
        rule.setPrincipalName(principalName);

        // Then
        assertThat(rule.getPrincipalName()).isEqualTo(principalName);
    }

    @Test
    void testChainedSetters() {
        // Given
        Long id = 1L;
        String name = "Admin Rule";
        AccessPrincipalType type = AccessPrincipalType.ROLE;

        // When
        AccessRule result = rule.setId(id)
                                .setName(name)
                                .setPrincipalType(type)
                                .setEnabled(false);

        // Then: chained calls return the same instance
        assertThat(result).isSameAs(rule);
        assertThat(rule.getId()).isEqualTo(id);
        assertThat(rule.getName()).isEqualTo(name);
        assertThat(rule.getPrincipalType()).isEqualTo(type);
        assertThat(rule.getEnabled()).isFalse();
    }

    @Test
    void testIdentityMethod() {
        // Given
        Long id = 42L;
        rule.setId(id);

        // When & Then
        assertThat(rule.identity()).isEqualTo(id);
    }

    @Test
    void testIdentityMethodWithNullId() {
        // Given
        rule.setId(null);

        // When & Then
        assertThat(rule.identity()).isNull();
    }
}
