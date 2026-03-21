package cloud.xcan.angus.core.gm.interfaces.log.facade.dto;

import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户操作日志查询DTO")
public class UserOperationLogFindDto extends PageQuery {

  @Schema(description = "日志ID")
  private Long id;

  @Schema(description = "操作用户ID")
  private Long userId;

  @Schema(description = "操作用户名")
  private String username;

  @Schema(description = "操作类型：READ/CREATE/UPDATE/DELETE")
  private OperationAction action;

  @Schema(description = "资源类型")
  private ResourceType resourceType;

  @Schema(description = "资源ID")
  private String resourceId;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
