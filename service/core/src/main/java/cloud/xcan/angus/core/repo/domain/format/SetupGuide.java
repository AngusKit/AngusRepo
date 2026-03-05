package cloud.xcan.angus.core.repo.domain.format;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetupGuide {

  private String formatName;
  private String repositoryUrl;
  private String configSnippet;
  private Map<String, String> instructions = new LinkedHashMap<>();

  public SetupGuide(String formatName, String repositoryUrl, String configSnippet) {
    this.formatName = formatName;
    this.repositoryUrl = repositoryUrl;
    this.configSnippet = configSnippet;
  }

  public void addInstruction(String step, String content) {
    this.instructions.put(step, content);
  }
}
