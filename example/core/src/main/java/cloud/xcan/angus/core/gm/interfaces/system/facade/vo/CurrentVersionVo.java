package cloud.xcan.angus.core.gm.interfaces.system.facade.vo;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.VersionDetailVo.FeatureItem;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
@Schema(description = "当前版本信息")
public class CurrentVersionVo {

  @Schema(description = "版本号", example = "1.5.2")
  private String version;

  @Schema(description = "构建号", example = "20251219001")
  private String buildNumber;

  @Schema(description = "发布日期", example = "2025-12-15 12:00:00")
  private LocalDateTime releaseDate;

  @Schema(description = "运行环境", example = "production")
  private String environment;

  @Schema(description = "组件信息")
  private Map<String, Object> components;

  @Schema(description = "运行时长", example = "15天 6小时 23分钟")
  private String uptime;

  @Schema(description = "启动时间", example = "2025-12-04 10:00:00")
  private LocalDateTime startTime;

  @Schema(description = "应用编码", example = "angus-gm")
  private String appCode;

  @Schema(description = "版本类型", example = "ENTERPRISE")
  private EditionType editionType;

  @Schema(description = "特性列表")
  private List<FeatureItem> features;

}
