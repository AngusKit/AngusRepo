package cloud.xcan.angus.core.gm.domain.tag;

import cloud.xcan.angus.core.jpa.auditor.AuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * 标签分类实体
 */
@Getter
@Setter
@Entity
@Table(name = "gm_tag_category")
public class TagCategory extends AuditingEntity<TagCategory, Long> {

  @Id
  @Column(name = "id")
  private Long id;

  @Column(name = "code", length = 80, nullable = false)
  private String code;

  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Column(name = "description", length = 200)
  private String description;

  @Column(name = "is_system", nullable = false)
  private Boolean isSystem = false;

  /**
   * 包含的标签数量，计算字段，不存储在数据库中
   */
  @Transient
  private Integer tagCount = 0;

  @Override
  public Long identity() {
    return id;
  }
}
