package cloud.xcan.angus.core.gm.domain.tag;

import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "gm_tag")
public class Tag extends AuditingEntity<Tag, Long> {

  @Id
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "color", length = 100)
  private String color;

  @Column(name = "category_id")
  private Long categoryId;

  @Column(name = "description", length = 200)
  private String description;

  @Column(name = "is_system", nullable = false)
  private Boolean isSystem = false;

  @Override
  public Long identity() {
    return id;
  }
}
