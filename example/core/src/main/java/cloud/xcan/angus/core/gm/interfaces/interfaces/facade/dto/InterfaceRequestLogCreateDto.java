package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_KEY_LENGTH_X2;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH_X2;

import cloud.xcan.angus.api.enums.ApiType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.util.LinkedMultiValueMap;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "创建API请求日志DTO")
public class InterfaceRequestLogCreateDto {

  @Length(max = 100)
  @Schema(description = "请求ID")
  private String requestId;

  @Length(max = 100)
  @Schema(description = "远程地址")
  private String remote;

  @Length(max = 100)
  @Schema(description = "客户端ID")
  private String clientId;

  @Length(max = 100)
  @Schema(description = "客户端来源")
  private String clientSource;

  @Schema(description = "租户ID")
  private Long tenantId;

  @Length(max = 200)
  @Schema(description = "租户名称")
  private String tenantName;

  @Schema(description = "用户ID")
  private Long userId;

  @Length(max = 100)
  @Schema(description = "用户全名")
  private String fullName;

  @Length(max = MAX_KEY_LENGTH_X2)
  @Schema(description = "API密钥（脱敏）")
  private String apiKey;

  @Length(max = MAX_KEY_LENGTH_X2)
  @Schema(description = "API密钥ID")
  private String apiKeyId;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "服务编码")
  private String serviceCode;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Eureka服务名称")
  private String serviceName;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "Eureka实例ID")
  private String instanceId;

  @Schema(description = "API类型")
  private ApiType apiType;

  @NotBlank
  @Length(max = 20)
  @Schema(description = "请求方法", requiredMode = RequiredMode.REQUIRED)
  private String method;

  @NotBlank
  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "请求URI", requiredMode = RequiredMode.REQUIRED)
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

  @NotNull
  @Schema(description = "HTTP状态码", requiredMode = RequiredMode.REQUIRED)
  private Integer status;

  @Schema(description = "响应头")
  private LinkedMultiValueMap<String, String> responseHeaders;

  @Schema(description = "响应体")
  private String responseBody;

  @Schema(description = "响应时间")
  private LocalDateTime responseDate;

  @Schema(description = "响应大小（字节）")
  private Integer responseSize;

  @NotNull
  @Schema(description = "耗时（毫秒）", requiredMode = RequiredMode.REQUIRED)
  private Long elapsedMillis;

}
