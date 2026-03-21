package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.core.gm.domain.application.ApplicationSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ApplicationSearchRepoMysql extends SimpleSearchRepository<Application>
    implements ApplicationSearchRepo {

}
