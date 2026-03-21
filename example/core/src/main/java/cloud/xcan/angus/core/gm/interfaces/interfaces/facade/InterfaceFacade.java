package cloud.xcan.angus.core.gm.interfaces.interfaces.facade;

import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceCallStatsDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceDeprecateDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceFindDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceSyncDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceCallStatsVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceDeprecateVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceDetailVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceListVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceServiceVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceSyncVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceTagVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

public interface InterfaceFacade {

  /**
   * 同步服务的接口
   */
  InterfaceSyncVo sync(InterfaceSyncDto dto);

  /**
   * 同步所有服务的接口
   */
  InterfaceSyncVo syncAll();

  /**
   * 标记接口为废弃
   */
  InterfaceDeprecateVo deprecate(Long id, InterfaceDeprecateDto dto);

  /**
   * 获取接口详情
   */
  InterfaceDetailVo getDetail(Long id);

  /**
   * 分页查询接口列表
   */
  PageResult<InterfaceListVo> list(InterfaceFindDto dto);

  /**
   * 按服务查询接口列表
   */
  PageResult<InterfaceListVo> listByService(String serviceName, InterfaceFindDto dto);

  /**
   * 按标签查询接口列表
   */
  PageResult<InterfaceListVo> listByTag(String tag, InterfaceFindDto dto);

  /**
   * 获取所有服务列表（带接口数量）
   */
  List<InterfaceServiceVo> getServices();

  /**
   * 获取所有标签列表（带接口数量）
   */
  List<InterfaceTagVo> getTags();

  /**
   * 获取接口调用统计
   */
  InterfaceCallStatsVo getCallStats(Long id, InterfaceCallStatsDto dto);
}
