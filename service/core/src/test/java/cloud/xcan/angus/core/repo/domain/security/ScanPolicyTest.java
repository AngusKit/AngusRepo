package cloud.xcan.angus.core.repo.domain.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ScanPolicyTest {

    private ScanPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new ScanPolicy();
    }

    @Test
    void testBasicProperties() {
        // Given
        String id = "policy-001";
        String name = "Security Policy";
        String description = "Auto scan on push";

        // When
        ScanPolicy result = policy.setId(id)
                .setName(name)
                .setDescription(description)
                .setRepositoryId("repo-001")
                .setScanType(ScanType.VULNERABILITY)
                .setEnabled(true);

        // Then
        assertThat(result).isSameAs(policy);
        assertThat(policy.getId()).isEqualTo(id);
        assertThat(policy.getName()).isEqualTo(name);
        assertThat(policy.getDescription()).isEqualTo(description);
        assertThat(policy.getRepositoryId()).isEqualTo("repo-001");
        assertThat(policy.getScanType()).isEqualTo(ScanType.VULNERABILITY);
        assertThat(policy.getEnabled()).isTrue();
    }

    @Test
    void testIdentity() {
        policy.setId("policy-001");
        assertThat(policy.identity()).isEqualTo("policy-001");
    }

    @Test
    void testDefaultValues() {
        assertThat(policy.getEnabled()).isTrue();
        assertThat(policy.getScanOnPush()).isFalse();
        assertThat(policy.getAutoBlock()).isFalse();
    }

    @Test
    void testScheduleAndThreshold() {
        // When
        policy.setScheduleCron("0 0 * * * ?")
                .setSeverityThreshold(VulnerabilitySeverity.HIGH)
                .setAutoBlock(true)
                .setScanOnPush(true);

        // Then
        assertThat(policy.getScheduleCron()).isEqualTo("0 0 * * * ?");
        assertThat(policy.getSeverityThreshold()).isEqualTo(VulnerabilitySeverity.HIGH);
        assertThat(policy.getAutoBlock()).isTrue();
        assertThat(policy.getScanOnPush()).isTrue();
    }

    @Test
    void testAuditFields() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        policy.setCreatedBy(1001L)
                .setCreatedDate(now)
                .setModifiedBy(1002L)
                .setModifiedDate(now);

        // Then
        assertThat(policy.getCreatedBy()).isEqualTo(1001L);
        assertThat(policy.getCreatedDate()).isEqualTo(now);
        assertThat(policy.getModifiedBy()).isEqualTo(1002L);
        assertThat(policy.getModifiedDate()).isEqualTo(now);
    }

    @Test
    void testTransientFields() {
        policy.setRepositoryName("My Repository");
        assertThat(policy.getRepositoryName()).isEqualTo("My Repository");
    }
}
