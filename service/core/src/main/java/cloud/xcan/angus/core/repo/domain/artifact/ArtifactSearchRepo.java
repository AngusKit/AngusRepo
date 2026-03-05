package cloud.xcan.angus.core.repo.domain.artifact;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ArtifactSearchRepo extends CustomBaseRepository<Artifact> {
}
