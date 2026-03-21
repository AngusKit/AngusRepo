package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.domain.backup.BackupSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public class BackupSearchRepoMysql extends SimpleSearchRepository<Backup>
    implements BackupSearchRepo {

}
