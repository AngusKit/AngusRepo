package cloud.xcan.angus.core.gm.application.cmd.user;

import cloud.xcan.angus.core.gm.domain.user.UserToken;
import java.util.List;

/**
 * 用户令牌命令服务接口
 */
public interface UserTokenCmd {

  /**
   * 创建用户令牌（需验证用户密码）
   */
  UserToken create(Long userId, UserToken token, List<String> scopes, Integer expiresInDays,
      String password);

  /**
   * 更新令牌信息（仅名称和描述）
   */
  UserToken update(Long userId, Long tokenId, UserToken token);

  /**
   * 撤销令牌
   */
  UserToken revoke(Long userId, Long tokenId);

  /**
   * 删除令牌
   */
  void delete(Long userId, Long tokenId);
}
