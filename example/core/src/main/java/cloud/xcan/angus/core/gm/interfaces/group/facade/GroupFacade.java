package cloud.xcan.angus.core.gm.interfaces.group.facade;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupOwnerUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupOwnerUpdateVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupOwnerVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupStatsVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

public interface GroupFacade {

  /**
   * 创建组
   */
  GroupDetailVo create(GroupCreateDto dto);

  /**
   * 更新组
   */
  GroupDetailVo update(Long id, GroupUpdateDto dto);

  /**
   * 更新组状态
   */
  GroupDetailVo updateStatus(Long id, EnabledStatusUpdateDto dto);

  /**
   * 更新组负责人
   */
  GroupOwnerUpdateVo updateOwner(Long id, GroupOwnerUpdateDto dto);

  /**
   * 删除组
   */
  void delete(Long id);

  /**
   * 获取组详情
   */
  GroupDetailVo getDetail(Long id);

  /**
   * 分页查询组列表
   */
  PageResult<GroupDetailVo> list(GroupFindDto dto);

  /**
   * 获取组统计数据
   */
  GroupStatsVo getStats();

  /**
   * 获取用户所在的组列表
   */
  List<GroupOwnerVo> getGroupsByUser(Long userId);
}
