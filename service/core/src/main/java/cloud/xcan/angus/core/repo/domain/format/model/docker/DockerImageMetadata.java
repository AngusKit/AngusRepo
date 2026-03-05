package cloud.xcan.angus.core.repo.domain.format.model.docker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DockerImageMetadata {
  private String imageName;
  private String tag;
  private String digest;
  private String manifestMediaType;
  private Long totalSize;
  private String architecture;
  private String os;
  private String author;
  private List<String> exposedPorts;
  private List<String> volumes;
  private Map<String, String> labels;
  private List<DockerLayer> layers;
  private DockerConfig config;
  private LocalDateTime created;
}
