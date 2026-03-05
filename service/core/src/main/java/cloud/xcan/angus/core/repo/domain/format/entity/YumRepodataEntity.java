package cloud.xcan.angus.core.repo.domain.format.entity;

import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "yum_repodata")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class YumRepodataEntity extends TenantEntity<YumRepodataEntity, Long> {

  @Id
  private Long id;

  @Column(name = "repository_id", nullable = false)
  private Long repositoryId;

  @Column(name = "repomd_xml", columnDefinition = "TEXT")
  private String repomdXml;

  @Lob
  @Column(name = "primary_xml")
  private byte[] primaryXml;

  @Lob
  @Column(name = "filelists_xml")
  private byte[] filelistsXml;

  @Lob
  @Column(name = "other_xml")
  private byte[] otherXml;

  @Column(name = "last_generated")
  private LocalDateTime lastGenerated;

  @Override
  public Long identity() {
    return this.id;
  }
}
