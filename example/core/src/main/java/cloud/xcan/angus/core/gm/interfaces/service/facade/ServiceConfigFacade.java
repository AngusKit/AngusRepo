package cloud.xcan.angus.core.gm.interfaces.service.facade;

import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.EurekaConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.EurekaTestDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.EurekaConfigVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.EurekaTestVo;

public interface ServiceConfigFacade {

  /**
   * 获取Eureka配置
   */
  EurekaConfigVo getEurekaConfig();

  /**
   * 更新Eureka配置
   */
  EurekaConfigVo updateEurekaConfig(EurekaConfigUpdateDto dto);

  /**
   * 测试Eureka连接
   */
  EurekaTestVo testEurekaConnection(EurekaTestDto dto);

}
