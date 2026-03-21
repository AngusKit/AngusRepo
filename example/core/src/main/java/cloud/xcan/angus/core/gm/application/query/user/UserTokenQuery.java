package cloud.xcan.angus.core.gm.application.query.user;

import cloud.xcan.angus.core.gm.domain.user.UserToken;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.TokenQuotaVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 用户令牌查询服务接口 负责用户令牌的读操作
 */
public interface UserTokenQuery {

  /**
   * 根据ID查找令牌并验证所有权
   */
  UserToken findAndCheck(Long userId, Long tokenId);

  /**
   * 根据用户ID查询令牌列表
   */
  Page<UserToken> findByUserId(Long userId, GenericSpecification<UserToken> spec,
      PageRequest pageable);

  /**
   * 获取令牌配额统计
   */
  TokenQuotaVo getQuota(Long userId);

}
