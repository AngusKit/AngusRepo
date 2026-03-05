package cloud.xcan.angus.core.repo.domain.security;

import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "scan_task")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class ScanTask extends TenantEntity<ScanTask, String> {

    public static final int MAX_ID_LENGTH = 64;
    public static final int MAX_NAME_LENGTH = 255;
    public static final int MAX_DESC_LENGTH = 1000;

    @Id
    @Column(length = MAX_ID_LENGTH)
    private String id;

    @NotBlank
    @Column(name = "artifact_id", nullable = false, length = MAX_ID_LENGTH)
    private String artifactId;

    @NotBlank
    @Column(name = "repository_id", nullable = false, length = MAX_ID_LENGTH)
    private String repositoryId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "scan_type", nullable = false, length = 20)
    private ScanType scanType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ScanStatus status = ScanStatus.PENDING;

    @Column
    private Integer progress = 0;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "vulnerability_count")
    private Integer vulnerabilityCount = 0;

    @Column(name = "critical_count")
    private Integer criticalCount = 0;

    @Column(name = "high_count")
    private Integer highCount = 0;

    @Column(name = "medium_count")
    private Integer mediumCount = 0;

    @Column(name = "low_count")
    private Integer lowCount = 0;

    @Column(name = "error_message", length = 4000)
    private String errorMessage;

    @Column(name = "scan_results", columnDefinition = "JSON")
    private String scanResultsJson;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Transient
    private String artifactName;

    @Transient
    private String repositoryName;

    @Override
    public String identity() {
        return this.id;
    }

    public Long calculateDurationSeconds() {
        if (startTime != null && endTime != null) {
            return java.time.Duration.between(startTime, endTime).getSeconds();
        }
        return null;
    }

    public boolean isRunning() {
        return status != null && status.isRunning();
    }

    public boolean isFinished() {
        return status != null && status.isFinished();
    }
}
