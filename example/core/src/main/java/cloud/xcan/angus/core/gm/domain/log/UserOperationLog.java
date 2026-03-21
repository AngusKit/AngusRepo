package cloud.xcan.angus.core.gm.domain.log;

import cloud.xcan.angus.api.enums.DeviceType;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.domain.log.enums.ResponseStatus;
import cloud.xcan.angus.core.jpa.multitenancy.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户操作日志实体
 */
@Getter
@Setter
@Entity
@Table(name = "gm_user_operation_log")
public class UserOperationLog extends TenantEntity<UserOperationLog, Long> {

  @Id
  private Long id;

  /**
   * 操作用户ID
   */
  @Column(name = "user_id", nullable = false)
  private Long userId;

  /**
   * 操作用户名称
   */
  @Column(name = "user_name", nullable = false, length = 100)
  private String userName;

  /**
   * 操作类型
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "action", nullable = false, length = 20)
  private OperationAction action;

  /**
   * 资源类型
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "resource_type", nullable = false, length = 40)
  private ResourceType resourceType;

  /**
   * 资源ID
   */
  @Column(name = "resource_id")
  private Long resourceId;

  /**
   * 操作资源名称
   */
  @Column(name = "resource", nullable = false, length = 200)
  private String resource;

  /**
   * 操作IP地址(Principal.getLocationInfo().getIp())
   */
  @Column(name = "ip", length = 40)
  private String ip;

  /**
   * 用户代理信息(Principal.getDeviceInfo().getUserAgent())
   */
  @Column(name = "user_agent", length = 400)
  private String userAgent;

  /**
   * 操作地点(Principal.getLocationInfo().getCountry() + " " + Principal.getLocationInfo().getCity())
   */
  @Column(name = "location", length = 100)
  private String location;

  /**
   * 操作设备(Principal.getDeviceInfo().getDeviceType())
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "device", length = 20)
  private DeviceType device;

  /**
   * 操作设备ID(Principal.getDeviceInfo().getDeviceId())
   */
  @Column(name = "device_id", length = 80)
  private String deviceId;

  /**
   * 操作详情描述
   */
  @Column(name = "details", length = 1000)
  private String details;

  /**
   * 响应状态
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "response_status", nullable = false, length = 20)
  private ResponseStatus responseStatus;

  /**
   * 错误信息
   */
  @Column(name = "error_message", length = 1000)
  private String errorMessage;

  /**
   * 创建日期
   */
  @Column(name = "created_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
  private LocalDateTime createdDate;

  @Override
  public Long identity() {
    return this.id;
  }
}
