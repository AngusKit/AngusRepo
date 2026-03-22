package cloud.xcan.angus.core.repo.domain.security;

import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "scan_policy")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class ScanPolicy extends TenantAuditingEntity<ScanPolicy, String> {

    public static final int MAX_ID_LENGTH = 64;
    public static final int MAX_NAME_LENGTH = 255;
    public static final int MAX_DESC_LENGTH = 1000;

    @Id
    @Column(length = MAX_ID_LENGTH)
    private String id;

    @Column(nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    @Column(length = MAX_DESC_LENGTH)
    private String description;

    @Column(name = "repository_id", nullable = false, length = MAX_ID_LENGTH)
    private String repositoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "scan_type", nullable = false, length = 20)
    private ScanType scanType;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name = "scan_on_push")
    private Boolean scanOnPush = false;

    @Column(name = "schedule_cron", length = 100)
    private String scheduleCron;

    @Column(name = "severity_threshold", length = 20)
    @Enumerated(EnumType.STRING)
    private VulnerabilitySeverity severityThreshold;

    @Column(name = "auto_block")
    private Boolean autoBlock = false;

    @Column(name = "last_scan_time")
    private LocalDateTime lastScanTime;

    @Transient
    private String repositoryName;

    @Override
    public String identity() {
        return this.id;
    }
}
