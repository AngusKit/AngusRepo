package cloud.xcan.angus.core.gm.application.cmd.interfaces;

import cloud.xcan.angus.core.gm.domain.interfaces.Interface;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaInstance;

/**
 * 接口命令服务接口 用于管理接口的同步和废弃操作
 */
public interface InterfaceCmd {

  /**
   * 同步指定服务的接口信息 从Eureka服务注册中心获取指定服务的OpenAPI文档并同步到本地数据库
   */
  void sync(String serviceName);

  /**
   * 同步所有服务的接口信息 从Eureka服务注册中心获取所有服务的OpenAPI文档并同步到本地数据库
   */
  void syncAll();

  /**
   * 同步指定服务的接口信息
   */
  void syncServiceInterfaces(String serviceName, EurekaInstance instance);

  /**
   * 标记接口为废弃状态 设置接口的废弃状态和废弃说明
   */
  Interface deprecate(Long id, Boolean deprecated, String deprecationNote);

}

