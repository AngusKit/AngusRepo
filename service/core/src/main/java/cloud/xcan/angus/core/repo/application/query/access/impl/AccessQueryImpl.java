package cloud.xcan.angus.core.repo.application.query.access.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.application.query.access.AccessQuery;
import cloud.xcan.angus.core.repo.domain.access.AccessLogRepo;
import cloud.xcan.angus.core.repo.domain.access.AccessRule;
import cloud.xcan.angus.core.repo.domain.access.AccessRuleListRepo;
import cloud.xcan.angus.core.repo.domain.access.AccessRuleRepo;
import cloud.xcan.angus.core.repo.domain.access.AccessRuleSearchRepo;
import cloud.xcan.angus.core.repo.domain.access.AccessToken;
import cloud.xcan.angus.core.repo.domain.access.AccessTokenRepo;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.AccessStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.UserPermissionVo;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class AccessQueryImpl implements AccessQuery {

  @Resource
  private AccessRuleRepo accessRuleRepo;

  @Resource
  private AccessRuleListRepo accessRuleListRepo;

  @Resource
  private AccessRuleSearchRepo accessRuleSearchRepo;

  @Resource
  private AccessTokenRepo accessTokenRepo;

  @Resource
  private AccessLogRepo accessLogRepo;

  @Override
  public Page<AccessRule> findRules(GenericSpecification<AccessRule> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<AccessRule>>() {
      @Override
      protected Page<AccessRule> process() {
        return fullTextSearch
            ? accessRuleSearchRepo.find(spec.getCriteria(), pageable, AccessRule.class, match)
            : accessRuleListRepo.find(spec.getCriteria(), pageable, AccessRule.class, null);
      }
    }.execute();
  }

  @Override
  public Optional<AccessRule> findRuleById(Long id) {
    return accessRuleRepo.findById(id);
  }

  @Override
  public AccessRule findRuleAndCheck(Long id) {
    return accessRuleRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("访问规则不存在: " + id));
  }

  @Override
  public List<AccessToken> findTokensByRepositoryId(Long repositoryId) {
    return accessTokenRepo.findByRepositoryId(repositoryId);
  }

  @Override
  public boolean checkPermission(Long repositoryId, Long userId, String permission, String path) {
    List<AccessRule> rules = accessRuleRepo.findByRepositoryIdAndEnabled(repositoryId, true);
    for (AccessRule rule : rules) {
      if (rule.getPermissions() != null && rule.getPermissions().contains(permission)) {
        if (path == null || rule.getPaths() == null || rule.getPaths().contains(path)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public UserPermissionVo getUserPermissions(Long repositoryId, Long userId) {
    return new BizTemplate<UserPermissionVo>() {
      @Override
      protected UserPermissionVo process() {
        List<AccessRule> rules = accessRuleRepo.findByRepositoryIdAndEnabled(repositoryId, true);
        List<String> permissions = new ArrayList<>();
        for (AccessRule rule : rules) {
          if (rule.getPermissions() != null) {
            permissions.add(rule.getPermissions());
          }
        }
        UserPermissionVo vo = new UserPermissionVo();
        vo.setUserId(userId);
        vo.setPermissions(permissions);
        return vo;
      }
    }.execute();
  }

  @Override
  public AccessStatisticsVo getAccessStatistics(Long repositoryId) {
    return new BizTemplate<AccessStatisticsVo>() {
      @Override
      protected AccessStatisticsVo process() {
        AccessStatisticsVo stats = new AccessStatisticsVo();
        stats.setTotalRules(accessRuleRepo.countByRepositoryId(repositoryId));
        stats.setTotalTokens(accessTokenRepo.countByRepositoryId(repositoryId));
        long totalAccesses = accessLogRepo.countByRepositoryId(repositoryId);
        stats.setTotalAccesses(totalAccesses);
        if (totalAccesses > 0) {
          long successCount = accessLogRepo.countByRepositoryIdAndSuccess(repositoryId, true);
          stats.setSuccessRate((double) successCount / totalAccesses * 100);
        } else {
          stats.setSuccessRate(0.0);
        }
        return stats;
      }
    }.execute();
  }
}
