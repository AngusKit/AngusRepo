package cloud.xcan.angus.core.gm.interfaces.application.facade;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationFindDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationListVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationStatsVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagListVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

public interface ApplicationFacade {

  /**
   * 创建应用
   */
  ApplicationDetailVo create(ApplicationCreateDto dto);

  /**
   * 更新应用
   */
  ApplicationDetailVo update(Long id, ApplicationUpdateDto dto);

  /**
   * 更新应用状态（启用/禁用）
   */
  ApplicationDetailVo updateStatus(Long id, EnabledStatusUpdateDto dto);

  /**
   * 删除应用
   */
  void delete(Long id);

  /**
   * 获取应用详情
   */
  ApplicationDetailVo getDetail(Long id);

  /**
   * 分页查询应用列表
   */
  PageResult<ApplicationListVo> find(ApplicationFindDto dto);

  /**
   * 获取应用统计数据
   */
  ApplicationStatsVo getStats();

  /**
   * 获取可用标签列表
   */
  List<TagListVo> getAvailableTags();
}
