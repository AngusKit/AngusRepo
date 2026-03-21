package cloud.xcan.angus.core.gm.interfaces.user.facade.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "支持的语言")
public class LanguageVo {

  @Schema(description = "语言代码", example = "zh-CN")
  private String code;

  @Schema(description = "语言名称", example = "简体中文")
  private String name;

  @Schema(description = "本地名称", example = "简体中文")
  private String nativeName;
}
