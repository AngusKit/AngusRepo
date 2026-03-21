package cloud.xcan.angus.core.gm.interfaces.application.facade.dto;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.application.enums.ApplicationSource;
import cloud.xcan.angus.api.commonlink.application.enums.ApplicationType;
import cloud.xcan.angus.remote.OrderSort;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询应用请求参数")
public class ApplicationFindDto extends PageQuery {

  @Schema(description = "应用ID")
  private Long id;

  @Schema(description = "应用名称")
  private String name;

  @Schema(description = "应用类型")
  private ApplicationType type;

  @Schema(description = "应用来源")
  private ApplicationSource source;

  @Schema(description = "状态")
  private EnabledStatus status;

  @Schema(description = "排序顺序，值越小越靠前")
  private Integer sortOrder;

  @Schema(description = "标签筛选")
  private String tag;

  @Override
  public String getDefaultOrderBy() {
    return "sortOrder";
  }

  @Override
  public OrderSort getDefaultOrderSort() {
    return OrderSort.ASC;
  }
}
