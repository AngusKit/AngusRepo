package cloud.xcan.angus.core.repo.infra.persistence.mysql.format;

import cloud.xcan.angus.core.repo.domain.format.entity.YumRepodataEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("yumRepodataEntityRepo")
public interface YumRepodataEntityRepoMysql extends YumRepodataEntityRepo {
}
