package cloud.xcan.angus.core.gm.domain.interfaces;

import cloud.xcan.angus.api.enums.ApiType;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.util.LinkedMultiValueMap;

/**
 * API请求日志实体
 */
@Getter
@Setter
@Entity
@Table(name = "gm_interface_request_log")
public class InterfaceRequestLog extends TenantEntity<InterfaceRequestLog, Long> {

  @Id
  private Long id;

  @Column(name = "request_id", length = 40)
  private String requestId;

  @Column(name = "remote", length = 40)
  private String remote;

  @Column(name = "client_id", length = 100)
  private String clientId;

  @Column(name = "client_source", length = 40)
  private String clientSource;

  @Column(name = "tenant_name", length = 100)
  private String tenantName;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "user_name", length = 100)
  private String userName;

  @Column(name = "api_key", length = 80)
  private String apiKey;

  @Column(name = "api_key_id", length = 80)
  private String apiKeyId;

  @Column(name = "edition_type", length = 40)
  private String editionType;

  @Column(name = "application_code", length = 80)
  private String applicationCode;

  @Column(name = "service_code", length = 100)
  private String serviceCode;

  @Column(name = "service_name", length = 100)
  private String serviceName;

  /**
   * 为Eureka实例ID，格式：IP:PORT
   */
  @Column(name = "instance_id", length = 40)
  private String instanceId;

  @Enumerated(EnumType.STRING)
  @Column(name = "api_type", length = 20)
  private ApiType apiType;

  @Column(name = "method", nullable = false, length = 20)
  private String method;

  @Column(name = "uri", nullable = false, length = 400)
  private String uri;

  @Column(name = "request_date")
  private LocalDateTime requestDate;

  @Column(name = "query_params", columnDefinition = "text")
  private String queryParams;

  @Type(JsonType.class)
  @Column(name = "request_headers", columnDefinition = "json")
  private LinkedMultiValueMap<String, String> requestHeaders;

  @Column(name = "request_body", columnDefinition = "text")
  private String requestBody;

  @Column(name = "request_size")
  private Integer requestSize;

  @Column(name = "status", nullable = false)
  private Integer status;

  @Type(JsonType.class)
  @Column(name = "response_headers", columnDefinition = "json")
  private LinkedMultiValueMap<String, String> responseHeaders;

  @Column(name = "response_body", columnDefinition = "text")
  private String responseBody;

  @Column(name = "response_date")
  private LocalDateTime responseDate;

  @Column(name = "response_size")
  private Integer responseSize;

  @Column(name = "elapsed_millis", nullable = false)
  private Long elapsedMillis;

  @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  protected LocalDateTime createdDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
