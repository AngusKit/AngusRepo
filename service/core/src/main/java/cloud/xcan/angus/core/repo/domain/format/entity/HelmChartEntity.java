package cloud.xcan.angus.core.repo.domain.format.entity;

import cloud.xcan.angus.core.jpa.multitenancy.TenantAuditingEntity;
import cloud.xcan.angus.core.jpa.multitenancy.TenantListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "helm_chart")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class HelmChartEntity extends TenantAuditingEntity<HelmChartEntity, Long> {

  @Id
  private Long id;

  @Column(name = "repository_id", nullable = false)
  private Long repositoryId;

  @Column(name = "artifact_id_ref")
  private Long artifactIdRef;

  @Column(name = "name", nullable = false, length = 500)
  private String name;

  @Column(name = "version", nullable = false, length = 100)
  private String version;

  @Column(name = "app_version", length = 100)
  private String appVersion;

  @Column(name = "api_version", length = 10)
  private String apiVersion = "v2";

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "chart_type", length = 50)
  private String chartType = "application";

  @Column(name = "keywords", columnDefinition = "JSON")
  private String keywords;

  @Column(name = "home", length = 500)
  private String home;

  @Column(name = "sources", columnDefinition = "JSON")
  private String sources;

  @Column(name = "maintainers", columnDefinition = "JSON")
  private String maintainers;

  @Column(name = "icon", length = 500)
  private String icon;

  @Column(name = "kube_version", length = 100)
  private String kubeVersion;

  @Column(name = "dependencies", columnDefinition = "JSON")
  private String dependencies;

  @Column(name = "annotations", columnDefinition = "JSON")
  private String annotations;

  @Column(name = "digest")
  private String digest;

  @Column(name = "tgz_path", length = 1000)
  private String tgzPath;

  @Column(name = "tgz_size")
  private Long tgzSize;

  @Column(name = "values_yaml", columnDefinition = "TEXT")
  private String valuesYaml;

  @Column(name = "readme", columnDefinition = "TEXT")
  private String readme;

  @Override
  public Long identity() {
    return this.id;
  }
}
