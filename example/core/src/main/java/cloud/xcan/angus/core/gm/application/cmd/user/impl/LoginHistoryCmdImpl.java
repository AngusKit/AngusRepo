package cloud.xcan.angus.core.gm.application.cmd.user.impl;

import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.LoginHistoryCmd;
import cloud.xcan.angus.core.gm.domain.user.LoginHistory;
import cloud.xcan.angus.core.gm.domain.user.LoginHistoryRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

/**
 * 用户登录历史记录命令服务实现
 */
@Service
public class LoginHistoryCmdImpl extends CommCmd<LoginHistory, Long> implements LoginHistoryCmd {

  @Resource
  private LoginHistoryRepo loginHistoryRepo;

  @Override
  public LoginHistory create(LoginHistory loginHistory) {
    // 设置登录时间
    if (loginHistory.getLoginTime() == null) {
      loginHistory.setLoginTime(LocalDateTime.now());
    }
    insert(loginHistory);
    return loginHistory;
  }

  @Override
  public void deleteByUserId(Long userId) {
    loginHistoryRepo.deleteByUserId(userId);
  }

  @Override
  protected BaseRepository<LoginHistory, Long> getRepository() {
    return loginHistoryRepo;
  }
}
