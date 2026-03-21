package cloud.xcan.angus.core.gm.interfaces.user.facade;

import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserAvatarUploadDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserProfileUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserProfileVo;

/**
 * 用户个人信息门面接口
 */
public interface UserProfileFacade {

  /**
   * 更新个人信息
   */
  UserProfileVo updateProfile(UserProfileUpdateDto dto);

  /**
   * 获取个人信息详情
   */
  UserProfileVo getProfile();

  /**
   * 上传头像
   */
  UserProfileVo uploadAvatar(UserAvatarUploadDto dto);

  /**
   * 上传头像文件并返回URL（不更新任何用户，用于管理员编辑用户时上传头像）
   */
  String uploadAvatarUrl(UserAvatarUploadDto dto);

  /**
   * 删除头像
   */
  void deleteAvatar();
}
