package cloud.xcan.angus.core.gm.interfaces.service.facade;

import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceFindDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.ServiceInstanceStatusDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceHealthVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceInstanceStatusVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceListVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.ServiceStatsVo;
import java.util.List;

public interface ServiceFacade {

  /**
   * 刷新服务列表
   */
  List<ServiceListVo> refresh();

  /**
   * 更新服务实例状态
   */
  ServiceInstanceStatusVo updateInstanceStatus(String serviceName, String instanceId,
      ServiceInstanceStatusDto dto);

  /**
   * 获取服务详情
   */
  ServiceDetailVo getDetail(String serviceName);

  /**
   * 获取服务列表
   */
  List<ServiceListVo> list(ServiceFindDto dto);

  /**
   * 获取服务统计数据
   */
  ServiceStatsVo getStats();

  /**
   * 获取服务实例健康状态
   */
  ServiceHealthVo getInstanceHealth(String serviceName, String instanceId);

}
