package cloud.xcan.angus.core.gm.interfaces.service.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.setting.eureka.EurekaConfig;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.EurekaConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.EurekaTestDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.EurekaConfigVo;

public class ServiceConfigAssembler {

  public static EurekaConfig toDomain(EurekaConfigUpdateDto vo) {
    EurekaConfig config = new EurekaConfig();
    config.setServiceUrl(vo.getServiceUrl());
    config.setEnableAuth(vo.getEnableAuth());
    config.setUsername(vo.getUsername());
    config.setPassword(vo.getPassword());
    config.setSyncInterval(vo.getSyncInterval());
    config.setEnableSsl(vo.getEnableSsl());
    config.setConnectTimeout(vo.getConnectTimeout());
    config.setReadTimeout(vo.getReadTimeout());
    return config;
  }

  public static EurekaConfig toDomain(EurekaTestDto dto) {
    EurekaConfig config = new EurekaConfig();
    config.setServiceUrl(dto.getServiceUrl());
    config.setUsername(dto.getUsername());
    config.setPassword(dto.getPassword());
    config.setEnableSsl(dto.getEnableSsl());
    config.setConnectTimeout(dto.getConnectTimeout());
    config.setReadTimeout(dto.getReadTimeout());
    if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
      config.setEnableAuth(true);
    }
    return config;
  }

  public static EurekaConfigVo toVo(EurekaConfig config) {
    EurekaConfigVo vo = new EurekaConfigVo();
    vo.setServiceUrl(config.getServiceUrl());
    vo.setEnableAuth(config.getEnableAuth());
    vo.setUsername(config.getUsername());
    vo.setPassword(config.getPassword());
    vo.setSyncInterval(config.getSyncInterval());
    vo.setEnableSsl(config.getEnableSsl());
    vo.setConnectTimeout(config.getConnectTimeout());
    vo.setReadTimeout(config.getReadTimeout());
    return vo;
  }
}
