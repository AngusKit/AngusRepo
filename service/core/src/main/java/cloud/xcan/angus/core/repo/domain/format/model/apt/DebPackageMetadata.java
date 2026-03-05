package cloud.xcan.angus.core.repo.domain.format.model.apt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebPackageMetadata {
  private String packageName;
  private String version;
  private String architecture;
  private String distribution;
  private String component;
  private String section;
  private String priority;
  private Long installedSize;
  private String maintainer;
  private String description;
  private String depends;
  private String preDepends;
  private String recommends;
  private String suggests;
  private String conflicts;
  private String provides;
  private String replaces;
  private String breaks;
  private String homepage;
  private String filename;
  private Long size;
  private String md5sum;
  private String sha1;
  private String sha256;
}
