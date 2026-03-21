package cloud.xcan.angus.core.gm.application.query.user;

import cloud.xcan.angus.core.gm.domain.user.UserInvite;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 用户邀请查询服务接口
 */
public interface UserInviteQuery {

  /**
   * 根据ID查找邀请并检查是否存在
   */
  UserInvite findAndCheck(Long id);

  /**
   * 分页查询邀请列表
   */
  Page<UserInvite> find(GenericSpecification<UserInvite> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  /**
   * 验证邀请码有效性
   */
  UserInvite findAndCheck(String inviteCode);

  /**
   * 根据邀请码查找邀请（不校验状态）
   */
  Optional<UserInvite> findByInviteCode(String inviteCode);
}
