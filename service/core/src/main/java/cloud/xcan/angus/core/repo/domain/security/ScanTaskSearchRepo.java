package cloud.xcan.angus.core.repo.domain.security;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ScanTaskSearchRepo extends CustomBaseRepository<ScanTask> {
}
