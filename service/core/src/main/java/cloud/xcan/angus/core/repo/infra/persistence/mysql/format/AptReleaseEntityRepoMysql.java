package cloud.xcan.angus.core.repo.infra.persistence.mysql.format;

import cloud.xcan.angus.core.repo.domain.format.entity.AptReleaseEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("aptReleaseEntityRepo")
public interface AptReleaseEntityRepoMysql extends AptReleaseEntityRepo {
}
