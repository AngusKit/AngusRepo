package cloud.xcan.angus.core.repo.domain.format.entity;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface HelmChartEntityRepo extends BaseRepository<HelmChartEntity, Long> {

  List<HelmChartEntity> findByRepositoryId(Long repositoryId);

  Optional<HelmChartEntity> findByRepositoryIdAndNameAndVersion(Long repositoryId, String name, String version);

  List<HelmChartEntity> findByRepositoryIdAndName(Long repositoryId, String name);

  long countByRepositoryId(Long repositoryId);
}
