package cloud.xcan.angus.core.gm.infra.persistence.postgres.system;

import cloud.xcan.angus.core.gm.domain.system.SystemVersion;
import cloud.xcan.angus.core.gm.domain.system.SystemVersionRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemVersionRepoPostgres extends JpaRepository<SystemVersion, Long>,
    SystemVersionRepo {

}
