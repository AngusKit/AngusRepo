package cloud.xcan.angus.api.gm.user.dto;

import cloud.xcan.angus.api.commonlink.user.enums.TokenStatus;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询令牌列表请求参数")
public class TokensQueryDto extends PageQuery {

  @Schema(description = "授权应用ID")
  private Long appId;

  @Schema(description = "应用名称", example = "AngusGM")
  private String appCode;

  @Schema(description = "状态过滤", example = "ACTIVE")
  private TokenStatus status;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
