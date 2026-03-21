package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.interfaces.facade.internal.assembler.InterfacesAssembler.toInterfaceDeprecateVo;
import static cloud.xcan.angus.core.gm.interfaces.interfaces.facade.internal.assembler.InterfacesAssembler.toInterfaceServiceVo;
import static cloud.xcan.angus.core.gm.interfaces.interfaces.facade.internal.assembler.InterfacesAssembler.toInterfaceSyncVo;
import static cloud.xcan.angus.core.gm.interfaces.interfaces.facade.internal.assembler.InterfacesAssembler.toInterfaceTagVos;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.setting.eureka.EurekaConfig;
import cloud.xcan.angus.core.gm.application.cmd.interfaces.InterfaceCmd;
import cloud.xcan.angus.core.gm.application.query.interfaces.InterfaceQuery;
import cloud.xcan.angus.core.gm.application.query.interfaces.InterfaceRequestLogQuery;
import cloud.xcan.angus.core.gm.application.query.service.ServiceConfigQuery;
import cloud.xcan.angus.core.gm.domain.interfaces.Interface;
import cloud.xcan.angus.core.gm.domain.interfaces.TagCount;
import cloud.xcan.angus.core.gm.domain.interfaces.enums.InterfaceSyncAction;
import cloud.xcan.angus.core.gm.infra.eureka.EurekaClientService;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaApplication;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.InterfaceFacade;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceCallStatsDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceDeprecateDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceFindDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceSyncDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.internal.assembler.InterfacesAssembler;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceCallStatsVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceDeprecateVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceListVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceServiceVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceSyncVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceTagVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class InterfaceFacadeImpl implements InterfaceFacade {

  @Resource
  private InterfaceQuery interfaceQuery;

  @Resource
  private InterfaceCmd interfaceCmd;

  @Resource
  private InterfaceRequestLogQuery interfaceRequestLogQuery;

  @Resource
  private EurekaClientService eurekaClientService;

  @Resource
  private ServiceConfigQuery serviceConfigQuery;

  @Override
  public InterfaceSyncVo sync(InterfaceSyncDto dto) {
    String serviceName = dto.getServiceName();
    LocalDateTime syncTime = LocalDateTime.now();

    // 执行同步
    interfaceCmd.sync(serviceName);

    // 查询同步结果
    long totalInterfaces = interfaceQuery.countByServiceName(serviceName);
    long deprecatedInterfaces = interfaceQuery.countByServiceNameAndDeprecated(serviceName, true);

    // 根据lastSyncAction统计新增和更新的接口数量
    long newInterfaces = interfaceQuery.countByServiceNameAndLastSyncAction(serviceName,
        InterfaceSyncAction.CREATE);
    long updatedInterfaces = interfaceQuery.countByServiceNameAndLastSyncAction(serviceName,
        InterfaceSyncAction.UPDATE);

    return toInterfaceSyncVo(serviceName, syncTime, (int) totalInterfaces, (int) newInterfaces,
        (int) updatedInterfaces, (int) deprecatedInterfaces);
  }

  @Override
  public InterfaceSyncVo syncAll() {
    LocalDateTime syncTime = LocalDateTime.now();

    // 执行同步
    interfaceCmd.syncAll();

    // 查询所有服务的同步结果
    List<String> serviceNames = interfaceQuery.findDistinctServiceNames();
    long totalInterfaces = interfaceQuery.countTotal();

    // 统计所有服务的接口数量和废弃接口数量
    List<InterfaceSyncVo.ServiceSyncInfo> services = new ArrayList<>();
    long totalDeprecatedInterfaces = 0;
    long totalNewInterfaces = 0;
    long totalUpdatedInterfaces = 0;

    for (String serviceName : serviceNames) {
      long count = interfaceQuery.countByServiceName(serviceName);
      long deprecatedCount = interfaceQuery.countByServiceNameAndDeprecated(serviceName,
          true);
      long newCount = interfaceQuery.countByServiceNameAndLastSyncAction(serviceName,
          InterfaceSyncAction.CREATE);
      long updatedCount = interfaceQuery.countByServiceNameAndLastSyncAction(serviceName,
          InterfaceSyncAction.UPDATE);

      totalDeprecatedInterfaces += deprecatedCount;
      totalNewInterfaces += newCount;
      totalUpdatedInterfaces += updatedCount;

      InterfaceSyncVo.ServiceSyncInfo serviceInfo = new InterfaceSyncVo.ServiceSyncInfo();
      serviceInfo.setServiceName(serviceName);
      serviceInfo.setInterfaceCount((int) count);
      services.add(serviceInfo);
    }

    long newInterfaces = totalNewInterfaces;
    long updatedInterfaces = totalUpdatedInterfaces;

    return toInterfaceSyncVo(syncTime, (int) totalInterfaces, (int) newInterfaces,
        (int) updatedInterfaces, (int) totalDeprecatedInterfaces, services);
  }


  @Override
  public InterfaceDeprecateVo deprecate(Long id, InterfaceDeprecateDto dto) {
    Interface inter = interfaceCmd.deprecate(id,
        dto.getDeprecated() != null ? dto.getDeprecated() : false,
        dto.getDeprecationNote());
    return toInterfaceDeprecateVo(inter);
  }

  @Override
  public InterfaceDetailVo getDetail(Long id) {
    Interface inter = interfaceQuery.findAndCheck(id);
    return InterfacesAssembler.toDetailVo(inter);
  }

  @Override
  public PageResult<InterfaceListVo> list(InterfaceFindDto dto) {
    GenericSpecification<Interface> spec = InterfacesAssembler.getSpecification(dto);
    Page<Interface> page = interfaceQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, InterfacesAssembler::toListVo);
  }

  @Override
  public PageResult<InterfaceListVo> listByService(String serviceName, InterfaceFindDto dto) {
    GenericSpecification<Interface> spec = InterfacesAssembler.getSpecification(dto);
    Page<Interface> page = interfaceQuery.findByServiceName(
        serviceName, spec, dto.tranPage(), dto.fullTextSearch,
        getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, InterfacesAssembler::toListVo);
  }

  @Override
  public PageResult<InterfaceListVo> listByTag(String tag, InterfaceFindDto dto) {
    GenericSpecification<Interface> spec = InterfacesAssembler.getSpecification(dto);
    Page<Interface> page = interfaceQuery.findByTag(
        tag, spec, dto.tranPage(), dto.fullTextSearch,
        dto.fullTextSearch ? getMatchSearchFields(dto.getClass()) : null);
    return buildVoPageResult(page, InterfacesAssembler::toListVo);
  }

  @Override
  public List<InterfaceServiceVo> getServices() {
    // 获取所有不重复的服务名称
    List<String> serviceNames = interfaceQuery.findDistinctServiceNames();
    if (serviceNames.isEmpty()) {
      return new ArrayList<>();
    }

    // 获取Eureka配置（用于获取服务详细信息）
    EurekaConfig eurekaConfig = serviceConfigQuery.getEurekaConfig();

    List<InterfaceServiceVo> vos = new ArrayList<>();
    for (String serviceName : serviceNames) {
      EurekaApplication application = eurekaClientService.getApplication(eurekaConfig, serviceName);
      // 统计接口数量
      long interfaceCount = interfaceQuery.countByServiceName(serviceName);
      // 查询该服务的接口，获取最后同步时间
      GenericSpecification<Interface> spec = new GenericSpecification<>();
      Page<Interface> allInterfaces = interfaceQuery.findByServiceName(
          serviceName, spec, PageRequest.of(0, Integer.MAX_VALUE), false, null);

      InterfaceServiceVo vo = toInterfaceServiceVo(serviceName, (int) interfaceCount,
          allInterfaces, eurekaConfig, application);
      vos.add(vo);
    }
    return vos;
  }

  @Override
  public List<InterfaceTagVo> getTags() {
    // 使用数据库GROUP BY查询，在数据库层面完成统计，避免加载所有接口到内存
    List<TagCount> tagCounts = interfaceQuery.countGroupByTag();
    // 构建返回结果
    return toInterfaceTagVos(tagCounts);
  }

  @Override
  public InterfaceCallStatsVo getCallStats(Long id, InterfaceCallStatsDto dto) {
    // 获取接口信息
    Interface inter = interfaceQuery.findAndCheck(id);

    String serviceName = inter.getServiceName();
    String uri = inter.getPath();
    String method = inter.getMethod() != null ? inter.getMethod().name() : null;

    // 调用 Query 层获取统计数据
    InterfaceCallStatsVo vo = interfaceRequestLogQuery.getCallStats(serviceName, uri, method, dto);
    vo.setInterfaceId(id);
    return vo;
  }
}
