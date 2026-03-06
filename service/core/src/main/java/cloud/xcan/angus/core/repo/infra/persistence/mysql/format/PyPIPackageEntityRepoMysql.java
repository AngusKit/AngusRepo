package cloud.xcan.angus.core.repo.infra.persistence.mysql.format;

import cloud.xcan.angus.core.repo.domain.format.entity.PyPIPackageEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("pyPIPackageEntityRepo")
public interface PyPIPackageEntityRepoMysql extends PyPIPackageEntityRepo {
}
