package cloud.xcan.angus.core.repo.domain.format.entity;

import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "apt_release")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class AptReleaseEntity extends TenantEntity<AptReleaseEntity, Long> {

  @Id
  private Long id;

  @Column(name = "repository_id", nullable = false)
  private Long repositoryId;

  @Column(name = "distribution", nullable = false, length = 100)
  private String distribution;

  @Column(name = "release_content", columnDefinition = "TEXT")
  private String releaseContent;

  @Column(name = "release_gpg", columnDefinition = "TEXT")
  private String releaseGpg;

  @Column(name = "in_release", columnDefinition = "TEXT")
  private String inRelease;

  @Column(name = "last_generated")
  private LocalDateTime lastGenerated;

  @Override
  public Long identity() {
    return this.id;
  }
}
