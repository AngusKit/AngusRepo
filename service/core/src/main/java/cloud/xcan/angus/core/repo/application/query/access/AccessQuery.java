package cloud.xcan.angus.core.repo.application.query.access;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.domain.access.AccessRule;
import cloud.xcan.angus.core.repo.domain.access.AccessToken;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.AccessStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.UserPermissionVo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface AccessQuery {

  Page<AccessRule> findRules(GenericSpecification<AccessRule> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  Optional<AccessRule> findRuleById(Long id);

  AccessRule findRuleAndCheck(Long id);

  List<AccessToken> findTokensByRepositoryId(Long repositoryId);

  boolean checkPermission(Long repositoryId, Long userId, String permission, String path);

  UserPermissionVo getUserPermissions(Long repositoryId, Long userId);

  AccessStatisticsVo getAccessStatistics(Long repositoryId);
}
