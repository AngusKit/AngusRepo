package cloud.xcan.angus.core.repo.domain.repository;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RepoEntitySearchRepo extends CustomBaseRepository<RepoEntity> {
}
