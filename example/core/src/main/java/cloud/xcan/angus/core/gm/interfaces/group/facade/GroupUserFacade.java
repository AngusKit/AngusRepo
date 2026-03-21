package cloud.xcan.angus.core.gm.interfaces.group.facade;

import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUserAddDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUserRemoveDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUserTransferDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupUserAddVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupUserTransferVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupUserVo;
import cloud.xcan.angus.remote.PageResult;

public interface GroupUserFacade {

  /**
   * 添加组用户
   */
  GroupUserAddVo addUsers(Long groupId, GroupUserAddDto dto);

  /**
   * 转移组用户
   */
  GroupUserTransferVo transferUsers(Long groupId, GroupUserTransferDto dto);

  /**
   * 移除组用户
   */
  void removeUser(Long groupId, Long userId);

  /**
   * 批量移除组用户
   */
  void removeUsers(Long groupId, GroupUserRemoveDto dto);

  /**
   * 分页查询组用户列表
   */
  PageResult<GroupUserVo> listUsers(Long groupId, GroupUserFindDto dto);

  /**
   * 分页查询未加入指定组的用户列表
   */
  PageResult<GroupUserVo> listUsersNotInGroup(Long groupId, GroupUserFindDto dto);

}
