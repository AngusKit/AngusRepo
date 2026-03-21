package cloud.xcan.angus.core.gm.interfaces.user.facade;

import cloud.xcan.angus.api.gm.user.dto.UserInviteDto;
import cloud.xcan.angus.api.gm.user.dto.UserInviteFindDto;
import cloud.xcan.angus.api.gm.user.vo.UserInviteResendVo;
import cloud.xcan.angus.api.gm.user.vo.UserInviteVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserAcceptInviteDto;
import cloud.xcan.angus.api.gm.user.vo.UserDetailVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

/**
 * 用户邀请门面接口
 */
public interface UserInviteFacade {

  /**
   * 邀请用户，支持单邮箱、多邮箱批量邀请及链接邀请
   *
   * @return 创建的邀请列表，链接邀请返回1条，邮件邀请返回对应条数
   */
  List<UserInviteVo> inviteUser(UserInviteDto dto);

  /**
   * 取消邀请
   */
  void cancelInvite(Long id);

  /**
   * 重新发送邀请
   */
  UserInviteResendVo resendInvite(Long id);

  /**
   * 获取邀请列表
   */
  PageResult<UserInviteVo> listInvites(UserInviteFindDto dto);

  /**
   * 根据邀请码获取邀请信息
   */
  UserInviteVo getInviteByCode(String inviteCode);

  /**
   * 接收邀请并创建用户账号
   */
  UserDetailVo acceptInvite(UserAcceptInviteDto dto);

  /**
   * 拒绝邀请（被邀请人操作，通过邀请码）
   */
  void rejectInviteByCode(String inviteCode);
}
