package cloud.xcan.angus.core.repo.infra.persistence.postgres.format;

import cloud.xcan.angus.core.repo.domain.format.entity.HelmChartEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("helmChartEntityRepo")
public interface HelmChartEntityRepoPostgres extends HelmChartEntityRepo {
}
