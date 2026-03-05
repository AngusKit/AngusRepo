package cloud.xcan.angus.core.repo.domain.access;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AccessRuleSearchRepo extends CustomBaseRepository<AccessRule> {

}
