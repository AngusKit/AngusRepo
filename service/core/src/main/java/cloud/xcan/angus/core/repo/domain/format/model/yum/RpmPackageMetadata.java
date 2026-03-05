package cloud.xcan.angus.core.repo.domain.format.model.yum;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpmPackageMetadata {
  private String name;
  private String epoch;
  private String version;
  private String release;
  private String arch;
  private String evr;
  private String summary;
  private String description;
  private String url;
  private String license;
  private String vendor;
  private String group;
  private String packager;
  private String buildHost;
  private Long buildTime;
  private String sourceRpm;
  private Long installedSize;
  private Long archiveSize;
  private String checksum;
  private String checksumType;
  private String headerStart;
  private String headerEnd;
  private List<RpmDependency> requires;
  private List<RpmDependency> provides;
  private List<RpmDependency> conflicts;
  private List<RpmDependency> obsoletes;
  private List<RpmDependency> recommends;
  private List<RpmDependency> suggests;
  private List<String> files;
  private List<String> dirs;
  private List<RpmChangelog> changelogs;
}
