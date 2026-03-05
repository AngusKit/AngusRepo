package cloud.xcan.angus.core.repo.domain.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class SecurityEnumsTest {

    @Test
    void testScanStatus_Values() {
        assertThat(ScanStatus.values()).hasSize(5);
        assertThat(ScanStatus.PENDING.getValue()).isEqualTo("pending");
        assertThat(ScanStatus.SCANNING.getValue()).isEqualTo("scanning");
        assertThat(ScanStatus.COMPLETED.getValue()).isEqualTo("completed");
        assertThat(ScanStatus.FAILED.getValue()).isEqualTo("failed");
        assertThat(ScanStatus.CANCELLED.getValue()).isEqualTo("cancelled");
    }

    @Test
    void testScanStatus_FromValue() {
        assertThat(ScanStatus.fromValue("pending")).isEqualTo(ScanStatus.PENDING);
        assertThat(ScanStatus.fromValue("scanning")).isEqualTo(ScanStatus.SCANNING);
        assertThatThrownBy(() -> ScanStatus.fromValue("invalid"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testScanStatus_IsRunning() {
        assertThat(ScanStatus.PENDING.isRunning()).isTrue();
        assertThat(ScanStatus.SCANNING.isRunning()).isTrue();
        assertThat(ScanStatus.COMPLETED.isRunning()).isFalse();
    }

    @Test
    void testScanStatus_IsFinished() {
        assertThat(ScanStatus.COMPLETED.isFinished()).isTrue();
        assertThat(ScanStatus.FAILED.isFinished()).isTrue();
        assertThat(ScanStatus.CANCELLED.isFinished()).isTrue();
        assertThat(ScanStatus.PENDING.isFinished()).isFalse();
    }

    @Test
    void testScanType_Values() {
        assertThat(ScanType.values()).hasSize(4);
        assertThat(ScanType.VULNERABILITY.getValue()).isEqualTo("vulnerability");
        assertThat(ScanType.LICENSE.getValue()).isEqualTo("license");
        assertThat(ScanType.MALWARE.getValue()).isEqualTo("malware");
        assertThat(ScanType.FULL.getValue()).isEqualTo("full");
    }

    @Test
    void testScanType_FromValue() {
        assertThat(ScanType.fromValue("vulnerability")).isEqualTo(ScanType.VULNERABILITY);
        assertThatThrownBy(() -> ScanType.fromValue("invalid"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testScanType_Descriptions() {
        assertThat(ScanType.VULNERABILITY.getDescription()).isEqualTo("漏洞扫描");
        assertThat(ScanType.LICENSE.getDescription()).isEqualTo("许可证扫描");
        assertThat(ScanType.MALWARE.getDescription()).isEqualTo("恶意软件扫描");
        assertThat(ScanType.FULL.getDescription()).isEqualTo("全面扫描");
    }

    @Test
    void testVulnerabilitySeverity_Values() {
        assertThat(VulnerabilitySeverity.values()).hasSize(4);
        assertThat(VulnerabilitySeverity.CRITICAL.getValue()).isEqualTo("critical");
        assertThat(VulnerabilitySeverity.HIGH.getValue()).isEqualTo("high");
        assertThat(VulnerabilitySeverity.MEDIUM.getValue()).isEqualTo("medium");
        assertThat(VulnerabilitySeverity.LOW.getValue()).isEqualTo("low");
    }

    @Test
    void testVulnerabilitySeverity_FromValue() {
        assertThat(VulnerabilitySeverity.fromValue("critical")).isEqualTo(VulnerabilitySeverity.CRITICAL);
        assertThatThrownBy(() -> VulnerabilitySeverity.fromValue("invalid"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
