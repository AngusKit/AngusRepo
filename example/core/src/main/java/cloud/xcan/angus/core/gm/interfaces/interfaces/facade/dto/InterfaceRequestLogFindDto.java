package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API请求日志查询DTO")
public class InterfaceRequestLogFindDto extends PageQuery {

  @Schema(description = "日志ID")
  private Long id;

  @Schema(description = "API密钥ID")
  private String apiKeyId;

  @Schema(description = "Eureka服务名称")
  private String serviceName;

  @Schema(description = "Eureka实例ID")
  private String instanceId;

  @Schema(description = "应用编码")
  private Long applicationCode;

  @Schema(description = "请求方法：GET/POST/PUT/DELETE/PATCH")
  private String method;

  @Schema(description = "HTTP状态码")
  private Integer status;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
