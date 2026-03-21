package cloud.xcan.angus.core.gm.interfaces.service.facade.internal.assembler;

import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaApplication;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaApplications;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaInstance;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaInstanceInfo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceInstanceStatusDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceInstanceStatusVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceInstanceVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceListVo;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务模块数据转换器
 */
public class ServiceAssembler {

  public static List<ServiceListVo> toServiceListVoList(EurekaApplications eurekaApplications) {
    if (eurekaApplications == null || eurekaApplications.getApplications() == null
        || eurekaApplications.getApplications().getApplication() == null) {
      return new ArrayList<>();
    }

    return eurekaApplications.getApplications().getApplication().stream()
        .map(ServiceAssembler::toServiceListVo)
        .collect(Collectors.toList());
  }

  public static ServiceListVo toServiceListVo(EurekaApplication app) {
    ServiceListVo vo = new ServiceListVo();
    vo.setServiceName(app.getName());
    vo.setDisplayName(app.getName());
    if (app.getInstance() != null) {
      vo.setInstances(app.getInstance().stream()
          .map(ServiceAssembler::toServiceInstanceVo)
          .collect(Collectors.toList()));
    } else {
      vo.setInstances(new ArrayList<>());
    }
    return vo;
  }

  public static ServiceInstanceStatusVo toServiceInstanceStatusVo(String instanceId,
      ServiceInstanceStatusDto dto, EurekaInstanceInfo instanceInfo) {
    ServiceInstanceStatusVo vo = new ServiceInstanceStatusVo();
    vo.setInstanceId(instanceId);
    vo.setModifiedDate(LocalDateTime.now());
    if (instanceInfo != null && instanceInfo.getInstance() != null) {
      vo.setStatus(instanceInfo.getInstance().getStatus());
    } else {
      vo.setStatus(dto.getStatus());
    }
    return vo;
  }

  public static ServiceDetailVo toServiceEmptyDetailVo(String serviceName) {
    ServiceDetailVo vo = new ServiceDetailVo();
    vo.setServiceName(serviceName);
    vo.setDisplayName(serviceName);
    vo.setTotalInstances(0);
    vo.setUpInstances(0);
    vo.setDownInstances(0);
    vo.setInstances(new ArrayList<>());
    return vo;
  }

  public static ServiceDetailVo toServiceDetailVo(EurekaApplication app) {
    ServiceDetailVo vo = new ServiceDetailVo();
    vo.setServiceName(app.getName());
    vo.setDisplayName(app.getName());

    if (app.getInstance() != null && !app.getInstance().isEmpty()) {
      List<ServiceInstanceVo> instances = app.getInstance().stream()
          .map(ServiceAssembler::toServiceInstanceVo)
          .collect(Collectors.toList());

      vo.setTotalInstances(instances.size());
      vo.setUpInstances((int) instances.stream()
          .filter(i -> "UP".equals(i.getStatus()))
          .count());
      vo.setDownInstances(vo.getTotalInstances() - vo.getUpInstances());
      vo.setInstances(instances);
    } else {
      vo.setTotalInstances(0);
      vo.setUpInstances(0);
      vo.setDownInstances(0);
      vo.setInstances(new ArrayList<>());
    }
    return vo;
  }

  public static ServiceInstanceVo toServiceInstanceVo(EurekaInstance instance) {
    ServiceInstanceVo vo = new ServiceInstanceVo();
    vo.setInstanceId(instance.getInstanceId());
    vo.setHostName(instance.getHostName());
    vo.setIpAddr(instance.getIpAddr());
    if (instance.getPort() != null) {
      vo.setPort(instance.getPort().getValue());
    }
    if (instance.getSecurePort() != null) {
      vo.setSecurePort(instance.getSecurePort().getValue());
    }
    vo.setStatus(instance.getStatus());
    vo.setHealthCheckUrl(instance.getHealthCheckUrl());
    vo.setStatusPageUrl(instance.getStatusPageUrl());
    vo.setHomePageUrl(instance.getHomePageUrl());
    vo.setMetadata(instance.getMetadata());

    // 计算最后心跳时间
    if (instance.getLeaseInfo() != null
        && instance.getLeaseInfo().getLastRenewalTimestamp() != null) {
      LocalDateTime lastHeartbeat = LocalDateTime.ofInstant(
          Instant.ofEpochMilli(instance.getLeaseInfo().getLastRenewalTimestamp()),
          ZoneId.systemDefault());
      vo.setLastHeartbeat(lastHeartbeat);
    }

    // 计算运行时间
    if (instance.getLeaseInfo() != null
        && instance.getLeaseInfo().getRegistrationTimestamp() != null) {
      long registrationTime = instance.getLeaseInfo().getRegistrationTimestamp();
      long currentTime = System.currentTimeMillis();
      long uptimeSeconds = (currentTime - registrationTime) / 1000;
      vo.setUptime(formatUptime(uptimeSeconds));
    }

    return vo;
  }

  private static String formatUptime(long seconds) {
    if (seconds < 60) {
      return seconds + "秒";
    } else if (seconds < 3600) {
      return (seconds / 60) + "分钟";
    } else if (seconds < 86400) {
      return (seconds / 3600) + "小时";
    } else {
      return (seconds / 86400) + "天";
    }
  }
}
