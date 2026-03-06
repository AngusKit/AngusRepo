package cloud.xcan.angus.core.repo.infra.persistence.mysql.format;

import cloud.xcan.angus.core.repo.domain.format.entity.NpmVersionEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("npmVersionEntityRepo")
public interface NpmVersionEntityRepoMysql extends NpmVersionEntityRepo {
}
