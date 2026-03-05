package cloud.xcan.angus.core.repo.domain.access;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AccessRuleRepo extends BaseRepository<AccessRule, Long> {

  List<AccessRule> findByRepositoryId(Long repositoryId);

  List<AccessRule> findByRepositoryIdAndEnabled(Long repositoryId, Boolean enabled);

  List<AccessRule> findByRepositoryIdAndPrincipalTypeAndPrincipalId(Long repositoryId,
      AccessPrincipalType principalType, String principalId);

  long countByRepositoryId(Long repositoryId);
}
