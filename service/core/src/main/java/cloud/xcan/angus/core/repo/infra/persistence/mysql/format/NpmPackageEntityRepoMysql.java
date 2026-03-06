package cloud.xcan.angus.core.repo.infra.persistence.mysql.format;

import cloud.xcan.angus.core.repo.domain.format.entity.NpmPackageEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("npmPackageEntityRepo")
public interface NpmPackageEntityRepoMysql extends NpmPackageEntityRepo {
}
