package cloud.xcan.angus.core.gm.infra.eureka;

import cloud.xcan.angus.api.commonlink.setting.eureka.EurekaConfig;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaApplication;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaApplications;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaInstanceInfo;
import java.util.Map;

/**
 * Eureka客户端服务接口 用于调用Eureka REST API
 */
public interface EurekaClientService {

  /**
   * 获取所有应用列表
   *
   * @param config Eureka配置
   * @return 应用列表
   */
  EurekaApplications getApplications(EurekaConfig config);

  /**
   * 获取指定应用信息
   *
   * @param config  Eureka配置
   * @param appName 应用名称
   * @return 应用信息
   */
  EurekaApplication getApplication(EurekaConfig config, String appName);

  /**
   * 获取指定实例信息
   *
   * @param config     Eureka配置
   * @param appName    应用名称
   * @param instanceId 实例ID
   * @return 实例信息
   */
  EurekaInstanceInfo getInstance(EurekaConfig config, String appName, String instanceId);

  /**
   * 更新实例状态
   *
   * @param config     Eureka配置
   * @param appName    应用名称
   * @param instanceId 实例ID
   * @param status     状态（UP, DOWN, OUT_OF_SERVICE等）
   */
  void updateInstanceStatus(EurekaConfig config, String appName, String instanceId,
      String status);

  /**
   * 获取实例健康状态
   *
   * @param config     Eureka配置
   * @param appName    应用名称
   * @param instanceId 实例ID
   * @return 健康状态信息
   */
  Map<String, Object> getInstanceHealth(EurekaConfig config, String appName,
      String instanceId);

  /**
   * 测试连接
   *
   * @param config Eureka配置
   * @return 是否连接成功
   */
  boolean testConnection(EurekaConfig config);
}
