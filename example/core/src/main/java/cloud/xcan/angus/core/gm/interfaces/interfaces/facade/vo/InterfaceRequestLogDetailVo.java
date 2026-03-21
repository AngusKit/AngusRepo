package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo;

import cloud.xcan.angus.api.enums.ApiType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.util.LinkedMultiValueMap;

@Data
@Schema(description = "API请求日志详情")
public class InterfaceRequestLogDetailVo {

  @Schema(description = "日志ID")
  private Long id;

  @Schema(description = "请求ID")
  private String requestId;

  @Schema(description = "远程地址")
  private String remote;

  @Schema(description = "客户端ID")
  private String clientId;

  @Schema(description = "客户端来源")
  private String clientSource;

  @Schema(description = "租户ID")
  private Long tenantId;

  @Schema(description = "租户名称")
  private String tenantName;

  @Schema(description = "用户ID")
  private Long userId;

  @Schema(description = "用户姓名")
  private String userName;

  @Schema(description = "API密钥（脱敏）")
  private String apiKey;

  @Schema(description = "API密钥ID")
  private String apiKeyId;

  @Schema(description = "应用版本类型")
  private String editionType;

  @Schema(description = "应用编码")
  private String applicationCode;

  @Schema(description = "服务编码")
  private String serviceCode;

  @Schema(description = "Eureka服务名称")
  private String serviceName;

  @Schema(description = "Eureka实例ID")
  private String instanceId;

  @Schema(description = "API类型")
  private ApiType apiType;

  @Schema(description = "请求方法")
  private String method;

  @Schema(description = "请求URI")
  private String uri;

  @Schema(description = "请求时间")
  private LocalDateTime requestDate;

  @Schema(description = "查询参数")
  private String queryParam;

  @Schema(description = "请求头")
  private LinkedMultiValueMap<String, String> requestHeaders;

  @Schema(description = "请求体")
  private String requestBody;

  @Schema(description = "请求大小（字节）")
  private Integer requestSize;

  @Schema(description = "HTTP状态码")
  private Integer status;

  @Schema(description = "响应头")
  private LinkedMultiValueMap<String, String> responseHeaders;

  @Schema(description = "响应体")
  private String responseBody;

  @Schema(description = "响应时间")
  private LocalDateTime responseDate;

  @Schema(description = "响应大小（字节）")
  private Integer responseSize;

  @Schema(description = "耗时（毫秒）")
  private Long elapsedMillis;

  @Schema(description = "创建时间")
  protected LocalDateTime createdDate;
}
