package cloud.xcan.angus.core.gm.interfaces.quota.facade.vo;

import cloud.xcan.angus.remote.NameJoinField;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "资源配额VO")
public class QuotaVo implements Serializable {

  @Schema(description = "资源编码")
  private String code;

  @Schema(description = "资源名称")
  private String name;

  @Schema(description = "所属应用编码")
  private String appCode;

  @Schema(description = "配额限额")
  private Long limit;

  @Schema(description = "已使用量")
  private Long used;

  @Schema(description = "单位")
  private String unit;

  @Schema(description = "资源说明")
  private String description;

  @Schema(description = "图标标识")
  private String icon;

  @Schema(description = "使用率（百分比），计算字段")
  private Double usagePercentage;

  @Schema(description = "启用状态")
  private Boolean enabled;

  /**
   * 是否许可控制
   * <p>
   * 如果为true，表示该配额由许可（License）控制，不允许通过系统界面修改。 许可控制的配额值由许可系统自动管理，用户只能查看，不能编辑。
   * </p>
   */
  @Schema(description = "是否许可控制")
  private Boolean isLicenseControl;

  @Schema(description = "租户ID")
  private Long tenantId;

  @Schema(description = "最后修改人ID")
  private Long modifiedBy;

  @NameJoinField(id = "modifiedBy", repository = "commonUserBaseRepo")
  private String modifier;

  @Schema(description = "最后修改时间")
  private LocalDateTime modifiedDate;

}
