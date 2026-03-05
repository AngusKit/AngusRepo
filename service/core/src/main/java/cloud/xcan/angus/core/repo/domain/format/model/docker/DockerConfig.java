package cloud.xcan.angus.core.repo.domain.format.model.docker;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DockerConfig {
  private String hostname;
  private String user;
  private List<String> env;
  private List<String> cmd;
  private List<String> entrypoint;
  private Map<String, Object> exposedPorts;
  private Map<String, Object> volumes;
  private String workingDir;
}
