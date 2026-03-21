package cloud.xcan.angus.core.gm.interfaces.application.facade.vo;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.application.enums.ApplicationSource;
import cloud.xcan.angus.api.commonlink.application.enums.ApplicationType;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.remote.vo.AuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "应用详情")
public class ApplicationDetailVo extends AuditingVo {

  @Schema(description = "应用ID")
  private Long id;

  @Schema(description = "应用编码")
  private String code;

  @Schema(description = "应用名称")
  private String name;

  @Schema(description = "显示名称")
  private String displayName;

  @Schema(description = "应用URL")
  private String url;

  @Schema(description = "描述")
  private String description;

  @Schema(description = "应用类型")
  private ApplicationType type;

  @Schema(description = "应用来源")
  private ApplicationSource source;

  @Schema(description = "状态")
  private EnabledStatus status;

  @Schema(description = "应用版本")
  private String version;

  @Schema(description = "版本类型")
  private EditionType editionType;

  @Schema(description = "排序顺序")
  private Integer sortOrder;

  @Schema(description = "标签列表")
  private List<String> tags;

  @Schema(description = "菜单数量")
  private int menuCount;

  @Schema(description = "角色数量")
  private int roleCount;

  @Schema(description = "用户数量")
  private int userCount;
}
