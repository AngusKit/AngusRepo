package cloud.xcan.angus.core.gm.interfaces.service.facade.internal;

import static cloud.xcan.angus.api.commonlink.setting.Setting.getDefaultEurekaConfig;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.eureka.EurekaConfig;
import cloud.xcan.angus.api.manager.SettingManager;
import cloud.xcan.angus.core.gm.application.cmd.setting.SettingCmd;
import cloud.xcan.angus.core.gm.infra.eureka.EurekaClientService;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaApplications;
import cloud.xcan.angus.core.gm.interfaces.service.facade.ServiceConfigFacade;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.EurekaConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.EurekaTestDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.internal.assembler.ServiceConfigAssembler;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.EurekaConfigVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.EurekaTestVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class ServiceConfigFacadeImpl implements ServiceConfigFacade {

  @Resource
  private SettingCmd settingCmd;

  @Resource
  private SettingManager settingManager;

  @Resource
  private EurekaClientService eurekaClientService;

  @Override
  public EurekaConfigVo updateEurekaConfig(EurekaConfigUpdateDto dto) {
    EurekaConfig config = ServiceConfigAssembler.toDomain(dto);
    EurekaConfig saved = settingCmd.update(config);
    return ServiceConfigAssembler.toVo(saved);
  }

  @Override
  public EurekaConfigVo getEurekaConfig() {
    Setting setting = settingManager.getSetting0(SettingKey.EUREKA_CONFIG);
    EurekaConfig config = nullSafe(setting != null ? setting.getEurekaConfig() : null,
        getDefaultEurekaConfig());
    return ServiceConfigAssembler.toVo(config);
  }

  @Override
  public EurekaTestVo testEurekaConnection(EurekaTestDto dto) {
    EurekaTestVo vo = new EurekaTestVo();
    long startTime = System.currentTimeMillis();
    try {
      EurekaConfig config = ServiceConfigAssembler.toDomain(dto);
      boolean connected = eurekaClientService.testConnection(config);
      vo.setConnected(connected);
      if (connected) {
        EurekaApplications applications = eurekaClientService.getApplications(config);
        if (applications != null && applications.getApplications() != null
            && applications.getApplications().getApplication() != null) {
          vo.setServicesCount(applications.getApplications().getApplication().size());
        } else {
          vo.setServicesCount(0);
        }
      } else {
        vo.setServicesCount(0);
      }
      long responseTime = System.currentTimeMillis() - startTime;
      vo.setResponseTime((int) responseTime);
    } catch (Exception e) {
      vo.setConnected(false);
      vo.setResponseTime((int) (System.currentTimeMillis() - startTime));
      vo.setServicesCount(0);
    }
    return vo;
  }

}
