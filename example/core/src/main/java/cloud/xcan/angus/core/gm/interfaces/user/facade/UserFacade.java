package cloud.xcan.angus.core.gm.interfaces.user.facade;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.api.gm.user.dto.ChangePasswordDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserBatchDeleteDto;
import cloud.xcan.angus.api.gm.user.dto.UserCreateDto;
import cloud.xcan.angus.api.gm.user.dto.UserFindDto;
import cloud.xcan.angus.api.gm.user.dto.UserLockDto;
import cloud.xcan.angus.api.gm.user.dto.UserPatchDto;
import cloud.xcan.angus.api.gm.user.dto.UserUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserCurrentDetailVo;
import cloud.xcan.angus.api.gm.user.vo.UserDetailVo;
import cloud.xcan.angus.api.gm.user.vo.UserListVo;
import cloud.xcan.angus.api.gm.user.vo.UserLockVo;
import cloud.xcan.angus.api.gm.user.vo.UserStatsVo;
import cloud.xcan.angus.api.gm.user.vo.UserStatusUpdateVo;
import cloud.xcan.angus.remote.PageResult;

public interface UserFacade {

  /**
   * 创建用户
   */
  UserDetailVo create(UserCreateDto dto);

  /**
   * 更新用户
   */
  UserDetailVo update(Long id, UserUpdateDto dto);

  /**
   * 部分更新用户
   */
  UserDetailVo patch(Long id, UserPatchDto dto);

  /**
   * 更新用户状态（启用/禁用）
   */
  UserStatusUpdateVo updateStatus(Long id, EnabledStatusUpdateDto dto);

  /**
   * 锁定/解锁用户
   */
  UserLockVo updateLock(Long id, UserLockDto dto);

  /**
   * 修改当前用户密码
   */
  void changeCurrentPassword(ChangePasswordDto dto);

  /**
   * 检查指定用户密码是否正确
   */
  void checkPassword(Long id, String password);

  /**
   * 删除用户
   */
  void delete(Long id);

  /**
   * 批量删除用户
   */
  void batchDelete(UserBatchDeleteDto dto);

  /**
   * 获取用户详情
   */
  UserDetailVo getDetail(Long id);

  /**
   * 获取当前用户详情
   */
  UserCurrentDetailVo getCurrent(String appCode, EditionType editionType);

  /**
   * 获取用户列表（分页）
   */
  PageResult<UserListVo> list(UserFindDto dto);

  /**
   * 获取用户统计数据
   *
   * @param appCode 应用编码，可选，指定时仅统计该应用下的用户
   */
  UserStatsVo getStats(String appCode);

}
