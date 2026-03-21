package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo;

import cloud.xcan.angus.remote.vo.AuditingVo;
import cloud.xcan.angus.spec.http.HttpMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "接口详情")
public class InterfaceDetailVo extends AuditingVo {

  @Schema(description = "接口ID")
  private Long id;

  @Schema(description = "服务名称")
  private String serviceName;

  @Schema(description = "服务显示名称")
  private String displayName;

  @Schema(description = "接口编码")
  private String code;

  @Schema(description = "接口路径")
  private String path;

  @Schema(description = "请求方法")
  private HttpMethod method;

  @Schema(description = "接口摘要")
  private String summary;

  @Schema(description = "接口描述")
  private String description;

  @Schema(description = "标签")
  private String tag;

  @Schema(description = "是否已废弃")
  private Boolean deprecated;

  @Schema(description = "版本号")
  private String version;

  @Schema(description = "最后同步时间")
  private LocalDateTime lastSyncTime;
}
