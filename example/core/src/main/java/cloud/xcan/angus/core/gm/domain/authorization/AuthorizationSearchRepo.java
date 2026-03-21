package cloud.xcan.angus.core.gm.domain.authorization;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AuthorizationSearchRepo extends CustomBaseRepository<Authorization> {
  // 继承全文搜索能力
}
