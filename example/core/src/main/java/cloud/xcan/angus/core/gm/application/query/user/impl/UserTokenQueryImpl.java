package cloud.xcan.angus.core.gm.application.query.user.impl;

import static cloud.xcan.angus.core.gm.domain.CommonConstant.MAX_USER_TOKEN_QUOTA;

import cloud.xcan.angus.api.commonlink.user.enums.TokenStatus;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.user.UserTokenQuery;
import cloud.xcan.angus.core.gm.domain.user.UserToken;
import cloud.xcan.angus.core.gm.domain.user.UserTokenRepo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.TokenQuotaVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * 用户令牌查询服务实现
 */
@Service
public class UserTokenQueryImpl implements UserTokenQuery {

  @Resource
  private UserTokenRepo userTokenRepo;

  @Override
  public UserToken findAndCheck(Long userId, Long tokenId) {
    return new BizTemplate<UserToken>() {
      @Override
      protected UserToken process() {
        UserToken token = userTokenRepo.findById(tokenId)
            .orElseThrow(() -> ResourceNotFound.of("令牌「{0}」不存在", new Object[]{tokenId}));

        // 验证所有权
        if (!userId.equals(token.getUserId())) {
          throw ResourceNotFound.of("令牌「{0}」不存在", new Object[]{tokenId});
        }

        // 检查过期状态
        if (TokenStatus.ACTIVE.equals(token.getStatus())
            && token.getExpiresAt() != null
            && token.getExpiresAt().isBefore(LocalDateTime.now())) {
          // 自动更新为过期状态
          token.setStatus(TokenStatus.EXPIRED);
          userTokenRepo.save(token);
        }

        return token;
      }
    }.execute();
  }

  @Override
  public Page<UserToken> findByUserId(Long userId, GenericSpecification<UserToken> spec,
      PageRequest pageable) {
    return new BizTemplate<Page<UserToken>>() {
      @Override
      protected Page<UserToken> process() {
        // 添加用户ID过滤条件
        spec.getCriteria().add(
            new cloud.xcan.angus.remote.search.SearchCriteria("userId", userId,
                cloud.xcan.angus.remote.search.SearchOperation.EQUAL));

        Page<UserToken> page = userTokenRepo.findAll(spec, pageable);

        // 检查并更新过期状态
        List<UserToken> tokens = page.getContent();
        LocalDateTime now = LocalDateTime.now();
        for (UserToken token : tokens) {
          if (TokenStatus.ACTIVE.equals(token.getStatus())
              && token.getExpiresAt() != null
              && token.getExpiresAt().isBefore(now)) {
            token.setStatus(TokenStatus.EXPIRED);
            userTokenRepo.save(token);
          }
        }

        return page;
      }
    }.execute();
  }

  @Override
  public TokenQuotaVo getQuota(Long userId) {
    return new BizTemplate<TokenQuotaVo>() {
      @Override
      protected TokenQuotaVo process() {
        TokenQuotaVo quota = new TokenQuotaVo();
        quota.setTotal(MAX_USER_TOKEN_QUOTA);

        long used = userTokenRepo.countByUserId(userId);
        quota.setUsed((int) used);
        quota.setAvailable((int) (MAX_USER_TOKEN_QUOTA - used));

        List<UserToken> tokens = userTokenRepo.findByUserId(userId);
        long activeCount = tokens.stream()
            .filter(t -> TokenStatus.ACTIVE.equals(t.getStatus())
                && (t.getExpiresAt() == null || t.getExpiresAt().isAfter(LocalDateTime.now())))
            .count();
        long expiredCount = tokens.stream()
            .filter(t -> TokenStatus.EXPIRED.equals(t.getStatus())
                || (TokenStatus.ACTIVE.equals(t.getStatus())
                && t.getExpiresAt() != null
                && t.getExpiresAt().isBefore(LocalDateTime.now())))
            .count();
        long revokedCount = tokens.stream()
            .filter(t -> TokenStatus.REVOKED.equals(t.getStatus()))
            .count();

        quota.setActiveCount((int) activeCount);
        quota.setExpiredCount((int) expiredCount);
        quota.setRevokedCount((int) revokedCount);

        return quota;
      }
    }.execute();
  }
}
