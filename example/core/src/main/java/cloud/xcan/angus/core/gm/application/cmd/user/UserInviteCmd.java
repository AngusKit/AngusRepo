package cloud.xcan.angus.core.gm.application.cmd.user;

import cloud.xcan.angus.core.gm.domain.user.UserInvite;

/**
 * 用户邀请命令服务接口
 */
public interface UserInviteCmd {

  /**
   * 创建用户邀请
   */
  UserInvite create(UserInvite userInvite);

  /**
   * 取消邀请
   */
  void cancel(Long id);

  /**
   * 重新发送邀请
   */
  UserInvite resend(Long id);

  /**
   * 更新用户邀请
   */
  void update0(UserInvite userInvite);

  /**
   * 拒绝邀请（被邀请人操作，通过邀请码）
   */
  void reject(String inviteCode);
}
