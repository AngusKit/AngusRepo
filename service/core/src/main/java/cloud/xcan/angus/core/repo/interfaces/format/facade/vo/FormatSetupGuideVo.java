package cloud.xcan.angus.core.repo.interfaces.format.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "客户端配置指南")
public class FormatSetupGuideVo implements Serializable {

  @Schema(description = "格式名称")
  private String formatName;

  @Schema(description = "仓库URL")
  private String repositoryUrl;

  @Schema(description = "配置代码片段")
  private String configSnippet;

  @Schema(description = "步骤说明")
  private Map<String, String> instructions;
}
