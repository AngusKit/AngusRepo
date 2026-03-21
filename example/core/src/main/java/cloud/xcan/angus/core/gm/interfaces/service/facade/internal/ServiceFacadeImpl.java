package cloud.xcan.angus.core.gm.interfaces.service.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.service.facade.internal.assembler.ServiceAssembler.toServiceEmptyDetailVo;
import static cloud.xcan.angus.core.gm.interfaces.service.facade.internal.assembler.ServiceAssembler.toServiceInstanceStatusVo;

import cloud.xcan.angus.api.commonlink.setting.eureka.EurekaConfig;
import cloud.xcan.angus.core.gm.application.query.service.ServiceConfigQuery;
import cloud.xcan.angus.core.gm.infra.eureka.EurekaClientService;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaApplication;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaApplications;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaInstanceInfo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.ServiceFacade;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceFindDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceInstanceStatusDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.internal.assembler.ServiceAssembler;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceHealthVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceInstanceStatusVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceListVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceStatsVo;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class ServiceFacadeImpl implements ServiceFacade {

  @Resource
  private ServiceConfigQuery serviceConfigQuery;

  @Resource
  private EurekaClientService eurekaClientService;

  @Override
  public List<ServiceListVo> refresh() {
    return list(null);
  }

  @Override
  public ServiceInstanceStatusVo updateInstanceStatus(String serviceName, String instanceId,
      ServiceInstanceStatusDto dto) {
    EurekaConfig eurekaConfig = serviceConfigQuery.getEurekaConfig();
    eurekaClientService.updateInstanceStatus(eurekaConfig, serviceName, instanceId,
        dto.getStatus());
    EurekaInstanceInfo instanceInfo =
        eurekaClientService.getInstance(eurekaConfig, serviceName, instanceId);
    return toServiceInstanceStatusVo(instanceId, dto, instanceInfo);
  }

  @Override
  public ServiceDetailVo getDetail(String serviceName) {
    EurekaConfig eurekaConfig = serviceConfigQuery.getEurekaConfig();
    EurekaApplication application = eurekaClientService.getApplication(eurekaConfig, serviceName);
    if (application != null) {
      return ServiceAssembler.toServiceDetailVo(application);
    } else {
      return toServiceEmptyDetailVo(serviceName);
    }
  }

  @Override
  public List<ServiceListVo> list(ServiceFindDto dto) {
    EurekaConfig eurekaConfig = serviceConfigQuery.getEurekaConfig();
    EurekaApplications applications = eurekaClientService.getApplications(eurekaConfig);
    if (applications == null || applications.getApplications() == null
        || applications.getApplications().getApplication() == null) {
      return new ArrayList<>();
    }

    List<ServiceListVo> serviceList = ServiceAssembler.toServiceListVoList(applications);

    if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
      String keyword = dto.getKeyword().toLowerCase();
      serviceList = serviceList.stream()
          .filter(service -> service.getServiceName() != null
              && service.getServiceName().toLowerCase().contains(keyword))
          .collect(Collectors.toList());
    }

    if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
      String status = dto.getStatus().toUpperCase();
      serviceList = serviceList.stream()
          .filter(service -> service.getInstances() != null
              && service.getInstances().stream()
              .anyMatch(instance -> status.equals(instance.getStatus())))
          .collect(Collectors.toList());
    }

    return serviceList;
  }

  @Override
  public ServiceStatsVo getStats() {
    ServiceStatsVo vo = new ServiceStatsVo();
    EurekaConfig eurekaConfig = serviceConfigQuery.getEurekaConfig();
    EurekaApplications applications = eurekaClientService.getApplications(eurekaConfig);
    if (applications != null && applications.getApplications() != null
        && applications.getApplications().getApplication() != null) {
      long totalServices = applications.getApplications().getApplication().size();
      long totalInstances = applications.getApplications().getApplication().stream()
          .flatMap(app -> app.getInstance() != null ? app.getInstance().stream()
              : Stream.empty())
          .count();
      long upInstances = applications.getApplications().getApplication().stream()
          .flatMap(app -> app.getInstance() != null ? app.getInstance().stream()
              : Stream.empty())
          .filter(instance -> "UP".equals(instance.getStatus()))
          .count();
      vo.setTotalServices(totalServices);
      vo.setTotalInstances(totalInstances);
      vo.setUpInstances(upInstances);
      vo.setDownInstances(totalInstances - upInstances);
    } else {
      vo.setTotalServices(0L);
      vo.setTotalInstances(0L);
      vo.setUpInstances(0L);
      vo.setDownInstances(0L);
    }
    return vo;
  }

  @Override
  public ServiceHealthVo getInstanceHealth(String serviceName, String instanceId) {
    ServiceHealthVo vo = new ServiceHealthVo();
    EurekaConfig eurekaConfig = serviceConfigQuery.getEurekaConfig();
    Map<String, Object> healthInfo =
        eurekaClientService.getInstanceHealth(eurekaConfig, serviceName, instanceId);
    if (healthInfo != null && !healthInfo.isEmpty()) {
      vo.setStatus((String) healthInfo.getOrDefault("status", "UNKNOWN"));
      vo.setDetails(healthInfo);
    } else {
      EurekaInstanceInfo instanceInfo =
          eurekaClientService.getInstance(eurekaConfig, serviceName, instanceId);
      if (instanceInfo != null && instanceInfo.getInstance() != null) {
        vo.setStatus(instanceInfo.getInstance().getStatus());
        vo.setDetails(new HashMap<>());
      } else {
        vo.setStatus("UNKNOWN");
        vo.setDetails(new HashMap<>());
      }
    }
    return vo;
  }

}
