package cloud.xcan.angus.core.repo.domain.activity;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ActivitySearchRepo extends CustomBaseRepository<Activity> {

}
