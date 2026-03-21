package cloud.xcan.angus.core.gm.application.cmd.user;

import cloud.xcan.angus.core.gm.domain.user.LoginHistory;

/**
 * 用户登录历史记录命令服务接口
 */
public interface LoginHistoryCmd {

  /**
   * 创建登录历史记录
   */
  LoginHistory create(LoginHistory loginHistory);

  /**
   * 根据用户ID删除登录历史记录
   */
  void deleteByUserId(Long userId);
}
