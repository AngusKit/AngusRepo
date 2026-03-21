package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.setting.eureka.EurekaConfig;
import cloud.xcan.angus.core.gm.domain.interfaces.Interface;
import cloud.xcan.angus.core.gm.domain.interfaces.TagCount;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaApplication;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaInstance;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceFindDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceDeprecateVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceListVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceServiceVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceSyncVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceSyncVo.ServiceSyncInfo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceTagVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.domain.Page;

public class InterfacesAssembler {

  public static InterfaceSyncVo toInterfaceSyncVo(String serviceName,
      LocalDateTime syncTime, int totalInterfaces, int newInterfaces,
      int updatedInterfaces, int deprecatedInterfaces) {
    InterfaceSyncVo vo = new InterfaceSyncVo();
    vo.setServiceName(serviceName);
    vo.setSyncTime(syncTime);
    vo.setTotalInterfaces(totalInterfaces);
    vo.setNewInterfaces(newInterfaces);
    vo.setUpdatedInterfaces(updatedInterfaces);
    vo.setDeprecatedInterfaces(deprecatedInterfaces);

    // 添加该服务的同步信息
    List<InterfaceSyncVo.ServiceSyncInfo> services = new ArrayList<>();
    InterfaceSyncVo.ServiceSyncInfo serviceInfo = new InterfaceSyncVo.ServiceSyncInfo();
    serviceInfo.setServiceName(serviceName);
    serviceInfo.setInterfaceCount(totalInterfaces);
    services.add(serviceInfo);
    vo.setServices(services);
    return vo;
  }

  public static InterfaceSyncVo toInterfaceSyncVo(LocalDateTime syncTime,
      int totalInterfaces, int newInterfaces, int updatedInterfaces,
      int totalDeprecatedInterfaces, List<ServiceSyncInfo> services) {
    InterfaceSyncVo vo = new InterfaceSyncVo();
    vo.setSyncTime(syncTime);
    vo.setTotalInterfaces(totalInterfaces);
    vo.setNewInterfaces(newInterfaces);
    vo.setUpdatedInterfaces(updatedInterfaces);
    vo.setDeprecatedInterfaces(totalDeprecatedInterfaces);
    vo.setServices(services);
    return vo;
  }

  public static InterfaceDeprecateVo toInterfaceDeprecateVo(Interface inter) {
    InterfaceDeprecateVo vo = new InterfaceDeprecateVo();
    vo.setId(inter.getId());
    vo.setDeprecated(inter.getDeprecated());
    vo.setModifiedDate(
        inter.getModifiedDate() != null ? inter.getModifiedDate() : LocalDateTime.now());
    return vo;
  }

  public static InterfaceServiceVo toInterfaceServiceVo(String serviceName,
      int interfaceCount, Page<Interface> allInterfaces, EurekaConfig eurekaConfig,
      EurekaApplication application) {
    InterfaceServiceVo vo = new InterfaceServiceVo();
    vo.setServiceName(serviceName);
    vo.setDisplayName(serviceName);
    vo.setInterfaceCount(interfaceCount);
    if (allInterfaces.hasContent()) {
      // 获取最新的同步时间
      LocalDateTime latestSyncTime = allInterfaces.getContent().stream()
          .map(Interface::getLastSyncTime)
          .filter(Objects::nonNull)
          .max(LocalDateTime::compareTo)
          .orElse(null);
      vo.setSyncTime(latestSyncTime);
    }

    // 尝试从Eureka获取baseUrl
    if (eurekaConfig != null) {
      try {
        if (application != null && application.getInstance() != null && !application.getInstance()
            .isEmpty()) {
          // 获取第一个UP状态的实例，或第一个实例
          EurekaInstance instance = application.getInstance().stream()
              .filter(inst -> "UP".equals(inst.getStatus()))
              .findFirst()
              .orElse(application.getInstance().get(0));
          // 设置baseUrl（使用homePageUrl）
          if (instance.getHomePageUrl() != null && !instance.getHomePageUrl().isEmpty()) {
            vo.setBaseUrl(instance.getHomePageUrl());
          }
          // 如果displayName为空，使用Eureka应用名称
          if (vo.getDisplayName() == null || vo.getDisplayName().equals(serviceName)) {
            vo.setDisplayName(application.getName());
          }
        }
      } catch (Exception e) {
        // Eureka查询失败，继续使用接口数据
      }
    }
    return vo;
  }

  public static List<InterfaceTagVo> toInterfaceTagVos(List<TagCount> tagCounts) {
    List<InterfaceTagVo> vos = new ArrayList<>();
    for (TagCount tagCount : tagCounts) {
      InterfaceTagVo vo = new InterfaceTagVo();
      vo.setName(tagCount.getTag());
      vo.setInterfaceCount(tagCount.getCount().intValue());
      vos.add(vo);
    }
    return vos;
  }

  public static InterfaceDetailVo toDetailVo(Interface inter) {
    InterfaceDetailVo vo = new InterfaceDetailVo();
    vo.setId(inter.getId());
    vo.setServiceName(inter.getServiceName());
    vo.setCode(inter.getCode());
    vo.setPath(inter.getPath());
    vo.setMethod(inter.getMethod());
    vo.setSummary(inter.getSummary());
    vo.setDescription(inter.getDescription());
    vo.setTag(inter.getTag());
    vo.setDeprecated(nullSafe(inter.getDeprecated(), false));
    vo.setVersion(inter.getVersion());
    vo.setLastSyncTime(inter.getLastSyncTime());
    return vo;
  }

  public static InterfaceListVo toListVo(Interface inter) {
    InterfaceListVo vo = new InterfaceListVo();
    vo.setId(inter.getId());
    vo.setServiceName(inter.getServiceName());
    vo.setCode(inter.getCode());
    vo.setPath(inter.getPath());
    vo.setMethod(inter.getMethod());
    vo.setSummary(inter.getSummary());
    vo.setDescription(inter.getDescription());
    vo.setTag(inter.getTag());
    vo.setDeprecated(nullSafe(inter.getDeprecated(), false));
    vo.setVersion(inter.getVersion());
    return vo;
  }

  public static GenericSpecification<Interface> getSpecification(InterfaceFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate", "lastSyncTime")
        .orderByFields("id", "createdDate", "modifiedDate", "path", "name")
        .matchSearchFields("name", "code", "path", "summary", "description")
        .build();
    return new GenericSpecification<>(filters);
  }
}
