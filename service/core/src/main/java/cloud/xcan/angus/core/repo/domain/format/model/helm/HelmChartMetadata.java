package cloud.xcan.angus.core.repo.domain.format.model.helm;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelmChartMetadata {
  private String name;
  private String version;
  private String appVersion;
  private String apiVersion;
  private String description;
  private String type;
  private List<String> keywords;
  private String home;
  private List<String> sources;
  private List<HelmMaintainer> maintainers;
  private String icon;
  private String kubeVersion;
  private List<HelmDependency> dependencies;
  private Map<String, String> annotations;
  private String digest;
  private String created;
  private List<String> urls;
}
