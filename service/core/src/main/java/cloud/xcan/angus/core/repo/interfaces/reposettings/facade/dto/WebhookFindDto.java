package cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "查询Webhook列表请求参数")
public class WebhookFindDto extends PageQuery {

  @Schema(description = "按启用状态筛选")
  private Boolean active;

  @Schema(description = "按名称筛选")
  private String name;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
