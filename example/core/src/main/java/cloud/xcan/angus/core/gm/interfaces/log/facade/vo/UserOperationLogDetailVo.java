package cloud.xcan.angus.core.gm.interfaces.log.facade.vo;

import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.domain.log.enums.ResponseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "用户操作日志详情")
public class UserOperationLogDetailVo {

  @Schema(description = "日志ID")
  private Long id;

  @Schema(description = "操作用户ID")
  private Long userId;

  @Schema(description = "操作用户名")
  private String userName;

  @Schema(description = "操作类型枚举值")
  private OperationAction action;

  @Schema(description = "资源类型枚举值")
  private ResourceType resourceType;

  @Schema(description = "资源ID")
  private Long resourceId;

  @Schema(description = "操作资源名称")
  private String resource;

  @Schema(description = "操作IP地址")
  private String ip;

  @Schema(description = "用户代理信息")
  private String userAgent;

  @Schema(description = "操作详情描述")
  private String details;

  @Schema(description = "响应状态枚举值")
  private ResponseStatus responseStatus;

  @Schema(description = "错误信息（如有）")
  private String errorMessage;

  @Schema(description = "租户ID")
  private Long tenantId;

  @Schema(description = "创建时间")
  private LocalDateTime createdDate;

}
